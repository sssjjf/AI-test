import ast
import os
import asyncio
import hashlib
from pathlib import Path
from typing import Dict, List, Set, Tuple, Optional, Any
from dataclasses import dataclass
from collections import defaultdict, deque
import json

@dataclass
class CodeDependency:
    """代码依赖关系"""
    source_file: str
    target_file: str
    dependency_type: str  # 'import', 'function_call', 'class_inheritance'
    details: Dict[str, Any]

class DependencyAwareAnalyzer:
    """依赖感知的代码分析器"""

    def __init__(self, model_client, max_tokens: int = 128000):
        self.model_client = model_client
        self.max_tokens = max_tokens
        self.dependency_graph = defaultdict(list)
        self.file_abstractions = {}
        self.analysis_cache = {}

    async def analyze_repository(self, repo_path: str) -> Dict[str, Any]:
        """分析整个代码仓库，考虑依赖关系"""
        print("开始分析代码仓库依赖关系...")

        # 1. 构建依赖图
        await self._build_dependency_graph(repo_path)

        # 2. 生成文件抽象
        await self._generate_file_abstractions(repo_path)

        # 3. 分层分析
        analysis_results = await self._layered_analysis(repo_path)

        # 4. 生成综合报告
        return await self._generate_comprehensive_report(analysis_results)

    async def _build_dependency_graph(self, repo_path: str):
        """构建代码依赖关系图"""
        print("构建依赖关系图...")
        repo_path_obj = Path(repo_path)

        for file_path in repo_path_obj.rglob("*.py"):
            if self._should_analyze_file(file_path):
                await self._analyze_file_dependencies(str(file_path), repo_path)

    async def _analyze_file_dependencies(self, file_path: str, repo_path: str):
        """分析单个文件的依赖关系"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            tree = ast.parse(content)
            relative_path = str(Path(file_path).relative_to(repo_path))

            # 分析导入语句
            for node in ast.walk(tree):
                if isinstance(node, ast.Import):
                    for alias in node.names:
                        await self._process_import(alias.name, relative_path, repo_path)

                elif isinstance(node, ast.ImportFrom):
                    if node.module:
                        await self._process_import(node.module, relative_path, repo_path)

        except Exception as e:
            print(f"分析文件依赖失败 {file_path}: {e}")

    async def _process_import(self, module_name: str, source_file: str, repo_path: str):
        """处理导入语句，解析依赖关系"""
        # 尝试解析导入对应的实际文件
        target_file = await self._resolve_import_to_file(module_name, source_file, repo_path)
        if target_file:
            dependency = CodeDependency(
                source_file=source_file,
                target_file=target_file,
                dependency_type="import",
                details={"module": module_name}
            )
            self.dependency_graph[source_file].append(dependency)

    async def _resolve_import_to_file(self, module_name: str, source_file: str, repo_path: str) -> Optional[str]:
        """将导入模块解析为实际文件路径"""
        repo_path_obj = Path(repo_path)
        source_dir = Path(source_file).parent

        # 可能的文件路径
        possible_paths = [
            source_dir / f"{module_name}.py",
            source_dir / module_name / "__init__.py",
            repo_path_obj / module_name.replace('.', '/') / "__init__.py",
            repo_path_obj / f"{module_name.replace('.', '/')}.py"
        ]

        for path in possible_paths:
            if path.exists():
                return str(path.relative_to(repo_path_obj))

        return None

    async def _generate_file_abstractions(self, repo_path: str):
        """为每个文件生成抽象表示"""
        print("生成文件抽象...")
        repo_path_obj = Path(repo_path)

        for file_path in repo_path_obj.rglob("*.py"):
            if self._should_analyze_file(file_path):
                relative_path = str(file_path.relative_to(repo_path))
                abstraction = await self._create_file_abstraction(file_path)
                self.file_abstractions[relative_path] = abstraction

    async def _create_file_abstraction(self, file_path: Path) -> Dict[str, Any]:
        """创建文件的抽象表示"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            tree = ast.parse(content)

            abstraction = {
                "file_path": str(file_path),
                "functions": [],
                "classes": [],
                "imports": [],
                "exports": [],
                "complexity_score": 0,
                "summary": ""
            }

            # 提取函数信息
            for node in ast.walk(tree):
                if isinstance(node, ast.FunctionDef):
                    func_info = await self._extract_function_info(node)
                    abstraction["functions"].append(func_info)
                    abstraction["exports"].append(f"function:{node.name}")

                elif isinstance(node, ast.ClassDef):
                    class_info = await self._extract_class_info(node)
                    abstraction["classes"].append(class_info)
                    abstraction["exports"].append(f"class:{node.name}")

            # 生成AI摘要
            abstraction["summary"] = await self._generate_ai_summary(content, abstraction)

            return abstraction

        except Exception as e:
            print(f"创建文件抽象失败 {file_path}: {e}")
            return {}

    async def _extract_function_info(self, func_node: ast.FunctionDef) -> Dict[str, Any]:
        """提取函数信息"""
        # 分析函数参数
        args = []
        for arg in func_node.args.args:
            args.append(arg.arg)

        # 分析函数体复杂度
        complexity = await self._calculate_function_complexity(func_node)

        return {
            "name": func_node.name,
            "args": args,
            "line_number": func_node.lineno,
            "complexity": complexity,
            "docstring": ast.get_docstring(func_node) or "",
            "is_async": isinstance(func_node, ast.AsyncFunctionDef)
        }

    async def _extract_class_info(self, class_node: ast.ClassDef) -> Dict[str, Any]:
        """提取类信息"""
        methods = []
        for node in class_node.body:
            if isinstance(node, ast.FunctionDef):
                method_info = await self._extract_function_info(node)
                methods.append(method_info)

        return {
            "name": class_node.name,
            "line_number": class_node.lineno,
            "methods": methods,
            "docstring": ast.get_docstring(class_node) or "",
            "bases": [base.id for base in class_node.bases if isinstance(base, ast.Name)]
        }

    async def _calculate_function_complexity(self, func_node: ast.FunctionDef) -> int:
        """计算函数复杂度"""
        complexity = 0
        for node in ast.walk(func_node):
            if isinstance(node, (ast.If, ast.While, ast.For, ast.Try, ast.With)):
                complexity += 1
            elif isinstance(node, ast.BoolOp):
                complexity += len(node.values) - 1
        return complexity

    async def _generate_ai_summary(self, content: str, abstraction: Dict) -> str:
        """使用AI生成文件摘要"""
        prompt = f"""
                请为以下代码文件生成一个简洁的摘要，重点描述：
                1. 文件的主要功能和职责
                2. 导出的主要函数和类
                3. 代码的设计模式和架构特点
                
                文件结构:
                - 函数: {[f['name'] for f in abstraction['functions']]}
                - 类: {[c['name'] for c in abstraction['classes']]}
                
                代码内容:{content[:2000]} # 限制内容长度
                请生成一个200字以内的摘要:
                """
        try:
            # 这里调用AI模型生成摘要
            # summary = await self.model_client.generate(prompt)
            # 暂时返回模拟摘要
            return f"包含 {len(abstraction['functions'])} 个函数和 {len(abstraction['classes'])} 个类的模块"
        except:
            return "无法生成摘要"

    async def _layered_analysis(self, repo_path: str) -> Dict[str, Any]:
        """分层分析策略"""
        print("执行分层分析...")

        # 1. 独立文件分析
        independent_results = await self._analyze_independent_files(repo_path)

        # 2. 依赖组分析
        dependency_groups = self._group_by_dependencies()
        group_results = await self._analyze_dependency_groups(dependency_groups, repo_path)

        # 3. 全局架构分析
        architecture_results = await self._analyze_architecture(repo_path)

        return {
            "independent_files": independent_results,
            "dependency_groups": group_results,
            "architecture": architecture_results
        }

    async def _analyze_independent_files(self, repo_path: str) -> Dict[str, Any]:
        """分析独立文件（无依赖或依赖较少的文件）"""
        results = {}
        repo_path_obj = Path(repo_path)

        independent_files = self._find_independent_files()

        for file_path in independent_files:
            full_path = repo_path_obj / file_path
            if full_path.exists():
                analysis = await self._analyze_single_file_with_context(full_path, {})
                results[file_path] = analysis

        return results

    def _find_independent_files(self) -> List[str]:
        """找到独立文件（依赖较少的文件）"""
        dependency_count = {}
        for file_path, deps in self.dependency_graph.items():
            dependency_count[file_path] = len(deps)

        # 返回依赖最少的文件
        sorted_files = sorted(dependency_count.items(), key=lambda x: x[1])
        return [file_path for file_path, count in sorted_files[:10]]  # 取前10个

    async def _analyze_single_file_with_context(self, file_path: Path, context_abstractions: Dict[str, Any]) -> Dict[str, Any]:
        """分析单个文件，提供依赖上下文"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # 构建分析上下文
            analysis_context = await self._build_analysis_context(file_path, context_abstractions)

            # 检查token数量
            estimated_tokens = len(content) // 4 + len(str(analysis_context)) // 4
            if estimated_tokens > self.max_tokens:
                return await self._analyze_large_file(file_path, analysis_context)

            # 使用AI分析
            prompt = self._build_analysis_prompt(content, analysis_context, str(file_path))
            analysis_result = await self._call_ai_model(prompt)

            return {
                "file_path": str(file_path),
                "analysis": analysis_result,
                "context_used": list(analysis_context.keys())
            }

        except Exception as e:
            return {"error": str(e), "file_path": str(file_path)}

    async def _build_analysis_context(self, file_path: Path, extra_context: Dict[str, Any]) -> Dict[str, Any]:
        """构建分析上下文"""
        context = {}
        relative_path = str(file_path)

        # 添加直接依赖的抽象
        if relative_path in self.dependency_graph:
            for dependency in self.dependency_graph[relative_path]:
                if dependency.target_file in self.file_abstractions:
                    context[dependency.target_file] = self.file_abstractions[dependency.target_file]

        # 添加上下文传递的抽象
        context.update(extra_context)

        # 限制上下文大小
        return self._limit_context_size(context)

    def _limit_context_size(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """限制上下文大小以避免token超限"""
        limited_context = {}
        total_size = 0
        max_context_size = self.max_tokens * 2  # 字符数限制

        for key, value in sorted(context.items(), key=lambda x: len(str(x))):
            value_size = len(str(value))
            if total_size + value_size <= max_context_size:
                limited_context[key] = value
                total_size += value_size
            else:
                # 对于太大的上下文，只保留关键信息
                limited_context[key] = self._create_minimal_abstraction(value)

        return limited_context

    def _create_minimal_abstraction(self, abstraction: Dict[str, Any]) -> Dict[str, Any]:
        """创建最小化的抽象表示"""
        return {
            "file_path": abstraction.get("file_path", ""),
            "summary": abstraction.get("summary", ""),
            "exports": abstraction.get("exports", [])[:5]  # 只保留前5个导出
        }


    def _build_analysis_prompt(self, code: str, context: Dict[str, Any], file_path: str) -> str:
        """构建分析提示词"""
        context_str = "\n".join([
            f"## {ctx_file}\n{ctx_data.get('summary', 'No summary')}\n"
            f"Exports: {', '.join(ctx_data.get('exports', []))}"
            for ctx_file, ctx_data in context.items()
        ])
        return f"""
                作为资深代码审查专家，请分析以下Python代码文件。在分析时，请考虑提供的依赖上下文。
                
                ## 依赖上下文:
                {context_str}
                
                ## 待分析文件: {file_path}
                
                ## 代码内容:
                ```python
                {code}
                审查重点:
                代码逻辑问题: 空指针、边界条件、异常处理
                
                性能问题: 死循环、内存泄漏、低效算法
                
                调用链路问题: 循环依赖、深度嵌套、不合理的依赖
                
                架构问题: 职责不清、耦合度过高
                
                请按以下格式提供分析结果:
                
                问题类型: [BUG|PERFORMANCE|ARCHITECTURE|SECURITY]
                
                严重程度: [CRITICAL|HIGH|MEDIUM|LOW]
                
                位置: 行号或函数名
                
                描述: 具体问题描述
                
                建议: 修复建议
                
                上下文影响: 该问题对依赖文件的影响
                """


    async def _call_ai_model(self, prompt: str) -> str:
        """调用AI模型进行分析"""
        # 这里实现与DeepSeek Coder的交互
        # 暂时返回模拟结果
        return "AI分析结果待实现"

    async def _analyze_large_file(self, file_path: Path, context: Dict[str, Any]) -> Dict[str, Any]:
        """分析大文件的分块策略"""
        print(f"使用分块策略分析大文件: {file_path}")
        # 将大文件按函数/类分块分析
        chunks = await self._split_file_into_chunks(file_path)
        chunk_results = []

        for chunk in chunks:
            chunk_analysis = await self._analyze_code_chunk(chunk, context)
            chunk_results.append(chunk_analysis)

        # 综合分块分析结果
        return await self._synthesize_chunk_analyses(chunk_results, str(file_path))


    async def _split_file_into_chunks(self, file_path: Path) -> List[Dict[str, Any]]:
        """将文件分割为分析块"""
        chunks = []
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            tree = ast.parse(content)

            # 按函数分块
            for node in ast.walk(tree):
                if isinstance(node, (ast.FunctionDef, ast.AsyncFunctionDef)):
                    chunk = {
                        "type": "function",
                        "name": node.name,
                        "content": ast.get_source_segment(content, node),
                        "line_number": node.lineno
                    }
                    chunks.append(chunk)

                elif isinstance(node, ast.ClassDef):
                    chunk = {
                        "type": "class",
                        "name": node.name,
                        "content": ast.get_source_segment(content, node),
                        "line_number": node.lineno
                    }
                    chunks.append(chunk)

            # 如果没有找到函数/类，按行分块
            if not chunks:
                lines = content.split('\n')
                for i in range(0, len(lines), 50):  # 每50行一个块
                    chunk = {
                        "type": "code_block",
                        "name": f"lines_{i+1}_{min(i+50, len(lines))}",
                        "content": '\n'.join(lines[i:i+50]),
                        "line_number": i + 1
                    }
                    chunks.append(chunk)
        except Exception as e:
            print(f"分块文件失败 {file_path}: {e}")
        return chunks

    async def _analyze_code_chunk(self, chunk: Dict[str, Any], context: Dict[str, Any]) -> Dict[str, Any]:
        """分析代码块"""
        prompt = f"""
        分析以下代码块，重点关注逻辑问题和性能问题:
        
        代码块类型: {chunk['type']}
        名称: {chunk['name']}
        行号: {chunk['line_number']}
        
        代码内容:
        
        python
        {chunk['content']}
        请提供具体的问题发现和建议:
        """
        # 调用AI模型分析代码块
        analysis = "代码块分析结果"
        return {
            "chunk_info": chunk,
            "analysis": analysis
        }


    async def _synthesize_chunk_analyses(self, chunk_results: List[Dict], file_path: str) -> Dict[str, Any]:
        """综合分块分析结果"""
    return {
        "file_path": file_path,
        "analysis_method": "chunked_analysis",
        "chunk_count": len(chunk_results),
        "synthesized_result": "综合所有代码块的分析结果",
        "chunk_details": chunk_results
    }

def _group_by_dependencies(self) -> List[List[str]]:
    """根据依赖关系分组文件"""
    # 使用强连通分量算法找到依赖组
    visited = set()
    groups = []

    for file_path in self.dependency_graph:
        if file_path not in visited:
            group = self._find_connected_component(file_path, visited)
            if len(group) > 1:  # 只包含有依赖关系的组
                groups.append(group)

    return groups

def _find_connected_component(self, start_file: str, visited: set) -> List[str]:
    """找到连通分量"""
    stack = [start_file]
    component = []

    while stack:
        file_path = stack.pop()
        if file_path not in visited:
            visited.add(file_path)
            component.append(file_path)

            # 添加依赖和被依赖的文件
            for dependency in self.dependency_graph.get(file_path, []):
                if dependency.target_file not in visited:
                    stack.append(dependency.target_file)

            # 找到依赖此文件的文件
            for other_file, dependencies in self.dependency_graph.items():
                if other_file not in visited:
                    for dep in dependencies:
                        if dep.target_file == file_path:
                            stack.append(other_file)
                            break

    return component

async def _analyze_dependency_groups(self, groups: List[List[str]], repo_path: str) -> Dict[str, Any]:
    """分析依赖组"""
    results = {}

    for i, group in enumerate(groups):
        print(f"分析依赖组 {i+1}/{len(groups)}: {len(group)} 个文件")
        group_analysis = await self._analyze_single_group(group, repo_path)
        results[f"group_{i+1}"] = group_analysis

    return results

async def _analyze_single_group(self, file_paths: List[str], repo_path: str) -> Dict[str, Any]:
    """分析单个依赖组"""
    # 为组内的所有文件构建组合上下文
    group_context = {}
    for file_path in file_paths:
        if file_path in self.file_abstractions:
            group_context[file_path] = self.file_abstractions[file_path]

    # 分析组内的每个文件，使用组内其他文件的抽象作为上下文
    group_results = {}
    for file_path in file_paths:
        full_path = Path(repo_path) / file_path
        if full_path.exists():
            # 创建不包含当前文件的上下文
            file_context = {k: v for k, v in group_context.items() if k != file_path}
            analysis = await self._analyze_single_file_with_context(full_path, file_context)
            group_results[file_path] = analysis

    return {
        "file_count": len(file_paths),
        "files": file_paths,
        "analyses": group_results,
        "group_issues": await self._find_cross_file_issues(group_results)
    }

async def _find_cross_file_issues(self, group_analyses: Dict[str, Any]) -> List[Dict[str, Any]]:
    """发现跨文件问题"""
    issues = []

    # 分析循环依赖
    cyclic_deps = self._find_cyclic_dependencies()
    for cycle in cyclic_deps:
        issues.append({
            "type": "ARCHITECTURE",
            "severity": "HIGH",
            "description": f"循环依赖: {' -> '.join(cycle)}",
            "suggestion": "重构代码消除循环依赖，引入接口或依赖注入"
        })

    # 分析接口不一致问题
    interface_issues = await self._find_interface_inconsistencies()
    issues.extend(interface_issues)

    return issues

def _find_cyclic_dependencies(self) -> List[List[str]]:
    """查找循环依赖"""
    cycles = []
    visited = set()
    recursion_stack = set()

    def dfs(file_path: str, path: List[str]):
        if file_path in recursion_stack:
            # 找到循环
            cycle_start = path.index(file_path)
            cycles.append(path[cycle_start:] + [file_path])
            return

        if file_path in visited:
            return

        visited.add(file_path)
        recursion_stack.add(file_path)
        current_path = path + [file_path]

        for dependency in self.dependency_graph.get(file_path, []):
            dfs(dependency.target_file, current_path)

        recursion_stack.remove(file_path)

    for file_path in self.dependency_graph:
        if file_path not in visited:
            dfs(file_path, [])

    return cycles

async def _find_interface_inconsistencies(self) -> List[Dict[str, Any]]:
    """查找接口不一致问题"""
    issues = []

    # 分析函数签名一致性
    function_signatures = defaultdict(list)

    for file_path, abstraction in self.file_abstractions.items():
        for func in abstraction.get("functions", []):
            signature = f"{func['name']}({', '.join(func['args'])})"
            function_signatures[func['name']].append({
                "file": file_path,
                "signature": signature,
                "line": func['line_number']
            })

    # 检查同名函数的不同签名
    for func_name, signatures in function_signatures.items():
        if len(signatures) > 1:
            unique_signatures = set(sig['signature'] for sig in signatures)
            if len(unique_signatures) > 1:
                issues.append({
                    "type": "DESIGN",
                    "severity": "MEDIUM",
                    "description": f"函数 '{func_name}' 在不同文件中有不同的签名",
                    "suggestion": "统一函数签名或重命名函数以避免混淆",
                    "locations": signatures
                })

    return issues

async def _analyze_architecture(self, repo_path: str) -> Dict[str, Any]:
    """全局架构分析"""
    print("执行全局架构分析...")

    # 使用文件抽象进行架构级分析
    architecture_context = self._create_architecture_context()

    prompt = f"""
    作为软件架构专家，请基于以下代码仓库的抽象信息进行架构分析:

    代码仓库概览:
    总文件数: {len(self.file_abstractions)}
    
    依赖关系数: {sum(len(deps) for deps in self.dependency_graph.values())}
    
    主要模块: {list(self.file_abstractions.keys())[:10]}
    
    文件抽象信息:
    {json.dumps(architecture_context, indent=2, ensure_ascii=False)}
    
    架构分析重点:
    模块划分是否合理
    
    依赖关系是否清晰
    
    是否存在架构异味
    
    性能瓶颈风险
    
    可维护性问题
    
    请提供架构级别的改进建议:
    """
    architecture_analysis = await self._call_ai_model(prompt)

    return {
        "file_count": len(self.file_abstractions),
        "dependency_count": sum(len(deps) for deps in self.dependency_graph.values()),
        "analysis": architecture_analysis,
        "key_metrics": await self._calculate_architecture_metrics()
    }

def _create_architecture_context(self) -> Dict[str, Any]:
    """创建架构分析上下文"""
    return {
        "file_abstractions": {
            file_path: {
                "summary": abs_data.get("summary", ""),
                "exports": abs_data.get("exports", []),
                "complexity": len(abs_data.get("functions", [])) + len(abs_data.get("classes", []))
            }
            for file_path, abs_data in list(self.file_abstractions.items())[:20]  # 限制数量
        },
        "dependency_patterns": self._analyze_dependency_patterns()
    }

def _analyze_dependency_patterns(self) -> Dict[str, Any]:
    """分析依赖模式"""
    inbound_deps = defaultdict(int)
    outbound_deps = defaultdict(int)

    for source_file, dependencies in self.dependency_graph.items():
        outbound_deps[source_file] = len(dependencies)
        for dep in dependencies:
            inbound_deps[dep.target_file] += 1

    return {
        "high_coupling_files": [
            file for file, count in inbound_deps.items()
            if count > 5  # 被多个文件依赖
        ],
        "unstable_files": [
            file for file, count in outbound_deps.items()
            if count > 8  # 依赖很多其他文件
        ]
    }

async def _calculate_architecture_metrics(self) -> Dict[str, float]:
    """计算架构指标"""
    total_files = len(self.file_abstractions)
    if total_files == 0:
        return {}

    # 计算平均依赖数
    total_dependencies = sum(len(deps) for deps in self.dependency_graph.values())
    avg_dependencies = total_dependencies / total_files

    # 计算抽象完整性
    abstraction_quality = sum(
        1 for abs_data in self.file_abstractions.values()
        if abs_data.get("summary")
    ) / total_files

    return {
        "average_dependencies_per_file": avg_dependencies,
        "abstraction_completeness": abstraction_quality,
        "file_count": total_files
    }

async def _generate_comprehensive_report(self, analysis_results: Dict[str, Any]) -> Dict[str, Any]:
    """生成综合报告"""
    print("生成综合报告...")

    # 收集所有问题
    all_issues = await self._collect_all_issues(analysis_results)

    return {
        "summary": {
            "total_files_analyzed": len(self.file_abstractions),
            "total_issues_found": len(all_issues),
            "analysis_strategy": "dependency_aware_layered"
        },
        "issue_categories": await self._categorize_issues(all_issues),
        "critical_issues": [issue for issue in all_issues if issue.get("severity") in ["CRITICAL", "HIGH"]],
        "architecture_assessment": analysis_results["architecture"],
        "dependency_analysis": {
            "total_dependencies": sum(len(deps) for deps in self.dependency_graph.values()),
            "cyclic_dependencies": self._find_cyclic_dependencies(),
            "dependency_groups": len(analysis_results["dependency_groups"])
        },
        "recommendations": await self._generate_recommendations(all_issues, analysis_results)
    }

async def _collect_all_issues(self, analysis_results: Dict[str, Any]) -> List[Dict[str, Any]]:
    """收集所有分析结果中的问题"""
    issues = []

    # 从独立文件分析收集问题
    for file_analysis in analysis_results["independent_files"].values():
        if "issues" in file_analysis:
            issues.extend(file_analysis["issues"])

    # 从依赖组分析收集问题
    for group_analysis in analysis_results["dependency_groups"].values():
        if "group_issues" in group_analysis:
            issues.extend(group_analysis["group_issues"])

        for file_analysis in group_analysis.get("analyses", {}).values():
            if "issues" in file_analysis:
                issues.extend(file_analysis["issues"])

    return issues

async def _categorize_issues(self, issues: List[Dict[str, Any]]) -> Dict[str, List]:
    """对问题进行分类"""
    categories = {
        "LOGIC": [],
        "PERFORMANCE": [],
        "ARCHITECTURE": [],
        "SECURITY": [],
        "DESIGN": []
    }

    for issue in issues:
        issue_type = issue.get("type", "DESIGN")
        if issue_type in categories:
            categories[issue_type].append(issue)
        else:
            categories["DESIGN"].append(issue)

    return categories

async def _generate_recommendations(self, issues: List[Dict[str, Any]], analysis_results: Dict[str, Any]) -> List[str]:
    """生成改进建议"""
    recommendations = []

    # 基于问题统计生成建议
    issue_counts = await self._categorize_issues(issues)

    if len(issue_counts["LOGIC"]) > 10:
        recommendations.append("发现较多逻辑错误，建议加强单元测试覆盖")

    if len(issue_counts["PERFORMANCE"]) > 5:
        recommendations.append("存在性能问题，建议进行性能 profiling 和优化")

    if analysis_results["architecture"].get("key_metrics", {}).get("average_dependencies_per_file", 0) > 5:
        recommendations.append("模块间依赖过多，建议重构以降低耦合度")

    cyclic_deps = self._find_cyclic_dependencies()
    if cyclic_deps:
        recommendations.append(f"发现 {len(cyclic_deps)} 个循环依赖，需要架构重构")

    return recommendations

def _should_analyze_file(self, file_path: Path) -> bool:
    """判断是否应该分析该文件"""
    exclude_patterns = ['test_', '_test.py', 'setup.py', 'conftest.py']
    exclude_dirs = ['tests', 'migrations', 'venv', '.git']

    file_name = file_path.name
    file_parts = file_path.parts

    # 跳过测试文件和配置