1. 调用链路分析引擎
python
# call_chain_analyzer.py
import ast
import inspect
from typing import Dict, List, Set, Tuple, Optional
from dataclasses import dataclass
from collections import defaultdict, deque
import asyncio

@dataclass
class CallNode:
    """调用链节点"""
    function_name: str
    file_path: str
    line_number: int
    caller: Optional['CallNode']
    callees: List['CallNode']
    code_snippet: str
    context: Dict

@dataclass
class CallChain:
    """完整的调用链"""
    entry_point: CallNode
    all_nodes: Dict[str, CallNode]
    depth: int
    complexity: float

class CallChainAnalyzer:
    """调用链路分析引擎"""
    
    def __init__(self):
        self.ast_parsers = {
            'python': PythonASTParser(),
            'javascript': JavaScriptASTParser(),
            'typescript': TypeScriptASTParser(),
            'java': JavaASTParser()
        }
        self.cross_file_analyzer = CrossFileAnalyzer()
        self.performance_predictor = PerformancePredictor()
        self.security_analyzer = SecurityChainAnalyzer()
    
    async def analyze_call_chain(self, entry_file: str, entry_function: str, repo_root: str) -> CallChain:
        """分析从入口函数开始的完整调用链"""
        
        # 构建调用链图
        call_graph = await self._build_call_graph(entry_file, entry_function, repo_root)
        
        # 深度分析每个节点
        analyzed_nodes = await self._deep_analyze_nodes(call_graph, repo_root)
        
        # 识别链路上的问题
        chain_issues = await self._analyze_chain_issues(analyzed_nodes)
        
        return CallChain(
            entry_point=analyzed_nodes[entry_function],
            all_nodes=analyzed_nodes,
            depth=self._calculate_chain_depth(analyzed_nodes),
            complexity=self._calculate_chain_complexity(analyzed_nodes)
        )
    
    async def _build_call_graph(self, start_file: str, start_function: str, repo_root: str) -> Dict[str, List[str]]:
        """构建调用关系图"""
        
        graph = defaultdict(list)
        visited = set()
        queue = deque([(start_file, start_function)])
        
        while queue:
            current_file, current_func = queue.popleft()
            node_key = f"{current_file}::{current_func}"
            
            if node_key in visited:
                continue
            visited.add(node_key)
            
            # 解析当前函数的调用
            callees = await self._parse_function_calls(current_file, current_func, repo_root)
            graph[node_key].extend(callees)
            
            # 将新的调用加入队列
            for callee_file, callee_func in callees:
                callee_key = f"{callee_file}::{callee_func}"
                if callee_key not in visited:
                    queue.append((callee_file, callee_func))
        
        return dict(graph)
    
    async def _parse_function_calls(self, file_path: str, function_name: str, repo_root: str) -> List[Tuple[str, str]]:
        """解析函数中的调用关系"""
        
        language = self._detect_language(file_path)
        parser = self.ast_parsers.get(language)
        
        if not parser:
            return []
        
        try:
            full_path = f"{repo_root}/{file_path}"
            with open(full_path, 'r', encoding='utf-8') as f:
                code_content = f.read()
            
            return await parser.extract_function_calls(code_content, function_name)
            
        except Exception as e:
            print(f"解析函数调用失败 {file_path}:{function_name}: {e}")
            return []
2. 多语言AST解析器
python
# ast_parsers.py
import ast
import esprima
import javalang
from typing import List, Tuple

class PythonASTParser:
    """Python AST解析器"""
    
    async def extract_function_calls(self, code: str, target_function: str) -> List[Tuple[str, str]]:
        """提取Python函数调用"""
        
        calls = []
        try:
            tree = ast.parse(code)
            
            # 查找目标函数的定义
            target_node = self._find_function_definition(tree, target_function)
            if not target_node:
                return calls
            
            # 遍历函数体中的调用
            for node in ast.walk(target_node):
                if isinstance(node, ast.Call):
                    call_info = self._parse_call_node(node)
                    if call_info:
                        calls.append(call_info)
                        
        except SyntaxError as e:
            print(f"Python语法解析错误: {e}")
            
        return calls
    
    def _find_function_definition(self, tree, function_name: str):
        """查找函数定义节点"""
        for node in ast.walk(tree):
            if (isinstance(node, (ast.FunctionDef, ast.AsyncFunctionDef)) and 
                node.name == function_name):
                return node
        return None
    
    def _parse_call_node(self, node: ast.Call) -> Tuple[str, str]:
        """解析调用节点"""
        if isinstance(node.func, ast.Name):
            # 直接函数调用: function()
            return ("current_file", node.func.id)
        elif isinstance(node.func, ast.Attribute):
            # 方法调用: obj.method()
            return ("current_file", node.func.attr)
        elif isinstance(node.func, ast.Call):
            # 链式调用: func()()
            return ("current_file", "anonymous")
        
        return None

class JavaScriptASTParser:
    """JavaScript AST解析器"""
    
    async def extract_function_calls(self, code: str, target_function: str) -> List[Tuple[str, str]]:
        """提取JavaScript函数调用"""
        
        calls = []
        try:
            parsed = esprima.parseScript(code, tolerant=True)
            
            # 查找目标函数
            function_node = self._find_function_declaration(parsed, target_function)
            if not function_node:
                return calls
            
            # 提取函数中的调用
            call_nodes = self._extract_call_expressions(function_node)
            for call in call_nodes:
                call_info = self._parse_js_call(call)
                if call_info:
                    calls.append(call_info)
                    
        except Exception as e:
            print(f"JavaScript解析错误: {e}")
            
        return calls
    
    def _find_function_declaration(self, parsed_ast, function_name: str):
        """查找函数声明"""
        # 实现JavaScript函数查找逻辑
        pass

class CrossFileAnalyzer:
    """跨文件调用分析"""
    
    def __init__(self):
        self.import_analyzers = {
            'python': PythonImportAnalyzer(),
            'javascript': JSImportAnalyzer(),
            'typescript': TSImportAnalyzer()
        }
    
    async def resolve_cross_file_calls(self, file_path: str, call_name: str, repo_root: str) -> List[Tuple[str, str]]:
        """解析跨文件调用"""
        
        language = self._detect_language(file_path)
        analyzer = self.import_analyzers.get(language)
        
        if not analyzer:
            return []
        
        return await analyzer.resolve_import(file_path, call_name, repo_root)

class PythonImportAnalyzer:
    """Python导入解析器"""
    
    async def resolve_import(self, file_path: str, call_name: str, repo_root: str) -> List[Tuple[str, str]]:
        """解析Python导入"""
        
        resolved_calls = []
        
        try:
            with open(f"{repo_root}/{file_path}", 'r', encoding='utf-8') as f:
                code = f.read()
            
            tree = ast.parse(code)
            
            # 分析导入语句
            imports = self._analyze_imports(tree)
            
            # 解析调用对应的实际文件
            for import_info in imports:
                if import_info['alias'] == call_name or import_info['original'] == call_name:
                    resolved_file = await self._find_imported_file(import_info, file_path, repo_root)
                    if resolved_file:
                        resolved_calls.append((resolved_file, call_name))
                        
        except Exception as e:
            print(f"导入解析失败 {file_path}: {e}")
        
        return resolved_calls
    
    def _analyze_imports(self, tree) -> List[Dict]:
        """分析导入语句"""
        imports = []
        
        for node in ast.walk(tree):
            if isinstance(node, ast.Import):
                for alias in node.names:
                    imports.append({
                        'type': 'import',
                        'module': alias.name,
                        'alias': alias.asname or alias.name.split('.')[-1],
                        'original': alias.name
                    })
            elif isinstance(node, ast.ImportFrom):
                module = node.module or ''
                for alias in node.names:
                    imports.append({
                        'type': 'from_import',
                        'module': module,
                        'alias': alias.asname or alias.name,
                        'original': f"{module}.{alias.name}" if module else alias.name
                    })
        
        return imports
3. 深度问题分析引擎
python
# deep_issue_analyzer.py
class DeepIssueAnalyzer:
    """深度问题分析引擎"""
    
    def __init__(self):
        self.issue_detectors = [
            PerformanceChainDetector(),
            SecurityChainDetector(),
            DataFlowDetector(),
            ResourceLeakDetector(),
            ErrorHandlingAnalyzer()
        ]
    
    async def analyze_chain_issues(self, call_chain: CallChain, repo_root: str) -> List[Dict]:
        """分析调用链中的深度问题"""
        
        all_issues = []
        
        # 并行运行各种问题检测器
        tasks = [
            detector.analyze_chain(call_chain, repo_root)
            for detector in self.issue_detectors
        ]
        
        results = await asyncio.gather(*tasks, return_exceptions=True)
        
        # 合并结果
        for result in results:
            if isinstance(result, list):
                all_issues.extend(result)
        
        # 按严重程度排序
        return self._prioritize_issues(all_issues)
    
    def _prioritize_issues(self, issues: List[Dict]) -> List[Dict]:
        """按优先级排序问题"""
        
        severity_weights = {
            'CRITICAL': 5,
            'HIGH': 4, 
            'MEDIUM': 3,
            'LOW': 2,
            'INFO': 1
        }
        
        return sorted(
            issues,
            key=lambda x: (
                severity_weights.get(x.get('severity', 'LOW'), 1),
                x.get('impact_scope', 'LOCAL') != 'LOCAL',
                -x.get('confidence', 0)
            ),
            reverse=True
        )

class PerformanceChainDetector:
    """性能链问题检测"""
    
    async def analyze_chain(self, call_chain: CallChain, repo_root: str) -> List[Dict]:
        """分析性能问题"""
        
        issues = []
        
        # 1. 检测深嵌套调用
        if call_chain.depth > 8:
            issues.append({
                'type': 'PERFORMANCE',
                'severity': 'MEDIUM',
                'title': '调用链过深',
                'description': f'调用链深度达到{call_chain.depth}层，可能影响性能',
                'suggestion': '考虑使用缓存或重构减少调用深度',
                'locations': [f"{node.file_path}:{node.line_number}" for node in call_chain.all_nodes.values()],
                'impact_scope': 'CHAIN',
                'confidence': 0.8
            })
        
        # 2. 检测循环调用
        cyclic_paths = self._detect_cyclic_calls(call_chain)
        for path in cyclic_paths:
            issues.append({
                'type': 'PERFORMANCE',
                'severity': 'HIGH', 
                'title': '循环调用风险',
                'description': f'检测到潜在的循环调用: {" -> ".join(path)}',
                'suggestion': '添加终止条件或使用缓存避免无限循环',
                'locations': path,
                'impact_scope': 'CHAIN',
                'confidence': 0.9
            })
        
        # 3. 检测重复计算
        duplicate_calls = await self._detect_duplicate_computations(call_chain, repo_root)
        issues.extend(duplicate_calls)
        
        return issues
    
    def _detect_cyclic_calls(self, call_chain: CallChain) -> List[List[str]]:
        """检测循环调用"""
        
        cycles = []
        visited = set()
        
        def dfs(node: CallNode, path: List[str]):
            node_key = f"{node.file_path}::{node.function_name}"
            
            if node_key in path:
                # 找到循环
                cycle_start = path.index(node_key)
                cycles.append(path[cycle_start:] + [node_key])
                return
            
            if node_key in visited:
                return
            
            visited.add(node_key)
            current_path = path + [node_key]
            
            for callee in node.callees:
                dfs(callee, current_path)
        
        dfs(call_chain.entry_point, [])
        return cycles

class SecurityChainDetector:
    """安全链问题检测"""
    
    async def analyze_chain(self, call_chain: CallChain, repo_root: str) -> List[Dict]:
        """分析安全问题"""
        
        issues = []
        
        # 1. 检测未经验证的数据流
        data_flow_issues = await self._analyze_data_flow_security(call_chain, repo_root)
        issues.extend(data_flow_issues)
        
        # 2. 检测权限检查缺失
        auth_issues = await self._analyze_authorization_chain(call_chain, repo_root)
        issues.extend(auth_issues)
        
        # 3. 检测敏感信息泄露
        leakage_issues = await self._detect_information_leakage(call_chain, repo_root)
        issues.extend(leakage_issues)
        
        return issues
    
    async def _analyze_data_flow_security(self, call_chain: CallChain, repo_root: str) -> List[Dict]:
        """分析数据流安全问题"""
        
        issues = []
        user_input_sources = set()
        sensitive_sinks = set()
        
        # 跟踪用户输入到敏感操作的路径
        for node_key, node in call_chain.all_nodes.items():
            # 检测用户输入源
            if self._is_user_input_source(node):
                user_input_sources.add(node_key)
            
            # 检测敏感操作
            if self._is_sensitive_sink(node):
                sensitive_sinks.add(node_key)
        
        # 查找从输入源到敏感操作的路径
        for source in user_input_sources:
            for sink in sensitive_sinks:
                path = self._find_path_between_nodes(call_chain, source, sink)
                if path and not self._has_validation_in_path(path):
                    issues.append({
                        'type': 'SECURITY',
                        'severity': 'HIGH',
                        'title': '未经验证的数据流',
                        'description': f'用户输入从 {source} 流向敏感操作 {sink} 缺少验证',
                        'suggestion': '在数据流路径中添加输入验证和清理',
                        'locations': path,
                        'impact_scope': 'CHAIN',
                        'confidence': 0.85
                    })
        
        return issues

class DataFlowDetector:
    """数据流分析"""
    
    async def analyze_data_flow(self, call_chain: CallChain, repo_root: str) -> List[Dict]:
        """分析数据流问题"""
        
        issues = []
        
        # 跟踪变量和数据的流动
        variable_flows = await self._track_variable_flow(call_chain, repo_root)
        
        # 检测数据不一致
        inconsistency_issues = self._detect_data_inconsistencies(variable_flows)
        issues.extend(inconsistency_issues)
        
        # 检测状态管理问题
        state_issues = self._detect_state_management_issues(variable_flows)
        issues.extend(state_issues)
        
        return issues
    
    async def _track_variable_flow(self, call_chain: CallChain, repo_root: str) -> Dict:
        """跟踪变量在调用链中的流动"""
        
        variable_flows = defaultdict(list)
        
        for node_key, node in call_chain.all_nodes.items():
            # 分析函数的参数和返回值
            function_analysis = await self._analyze_function_data_flow(node, repo_root)
            
            for var_name, flow_info in function_analysis.items():
                variable_flows[var_name].append({
                    'node': node_key,
                    'operation': flow_info['operation'],  # read, write, modify
                    'type': flow_info.get('type'),
                    'value_source': flow_info.get('source')
                })
        
        return dict(variable_flows)
4. AI增强的深度分析
python
# ai_chain_analyzer.py
class AIChainAnalyzer:
    """AI增强的调用链分析"""
    
    def __init__(self, ai_scanner):
        self.ai_scanner = ai_scanner
        self.chain_analyzer = CallChainAnalyzer()
    
    async def deep_analyze_with_ai(self, entry_file: str, entry_function: str, repo_root: str) -> Dict:
        """使用AI进行深度调用链分析"""
        
        # 1. 构建调用链
        call_chain = await self.chain_analyzer.analyze_call_chain(
            entry_file, entry_function, repo_root
        )
        
        # 2. 提取链路的完整上下文
        chain_context = self._build_chain_context(call_chain, repo_root)
        
        # 3. 使用AI分析完整链路
        ai_analysis = await self._analyze_chain_with_ai(chain_context)
        
        # 4. 结合规则引擎验证
        validated_issues = await self._validate_ai_findings(ai_analysis, call_chain)
        
        return {
            'call_chain': call_chain,
            'ai_analysis': ai_analysis,
            'validated_issues': validated_issues,
            'chain_metrics': self._calculate_chain_metrics(call_chain)
        }
    
    def _build_chain_context(self, call_chain: CallChain, repo_root: str) -> str:
        """构建调用链的完整上下文"""
        
        context_parts = []
        
        # 链路的整体结构
        context_parts.append("## 调用链整体结构")
        context_parts.append(f"- 入口点: {call_chain.entry_point.function_name}")
        context_parts.append(f"- 调用深度: {call_chain.depth}层")
        context_parts.append(f"- 涉及函数: {len(call_chain.all_nodes)}个")
        context_parts.append(f"- 复杂度评分: {call_chain.complexity:.2f}")
        
        # 详细的调用路径
        context_parts.append("\n## 详细调用路径")
        for i, (node_key, node) in enumerate(call_chain.all_nodes.items()):
            context_parts.append(f"{i+1}. {node_key} (L{node.line_number})")
            if node.code_snippet:
                context_parts.append(f"   代码片段: {node.code_snippet[:200]}...")
        
        # 数据流信息
        context_parts.append("\n## 数据流分析")
        data_flows = self._extract_data_flows(call_chain)
        for flow in data_flows:
            context_parts.append(f"- {flow}")
        
        return "\n".join(context_parts)
    
    async def _analyze_chain_with_ai(self, chain_context: str) -> Dict:
        """使用AI分析完整调用链"""
        
        prompt = f"""
作为资深软件架构师，请深度分析以下调用链路，识别潜在的系统性问题和优化机会。

{chain_context}

请从以下维度进行分析：

1. **架构设计问题**
   - 是否存在循环依赖？
   - 模块职责是否清晰？
   - 是否存在上帝对象或过重服务？

2. **性能瓶颈风险**
   - 哪些链路可能成为性能瓶颈？
   - 是否存在重复计算或冗余调用？
   - 缓存策略是否合理？

3. **安全风险链**
   - 敏感数据在整个链路中的处理是否安全？
   - 权限检查是否完整覆盖？
   - 是否存在潜在的攻击面？

4. **可维护性问题**
   - 链路的复杂度是否过高？
   - 错误处理机制是否一致？
   - 日志和监控覆盖是否完整？

5. **业务逻辑一致性**
   - 数据在不同层之间的转换是否一致？
   - 业务规则是否在链路中得到正确传递？

请按以下格式返回分析结果：
- 问题类别：[ARCHITECTURE|PERFORMANCE|SECURITY|MAINTAINABILITY|BUSINESS]
- 严重程度：[CRITICAL|HIGH|MEDIUM|LOW]
- 影响范围：[ENTIRE_CHAIN|PARTIAL_CHAIN|SINGLE_NODE]
- 问题描述：具体问题说明
- 修复建议：具体的改进方案
- 涉及节点：相关的函数/方法
- 置信度：分析置信度[0.0-1.0]
"""
        
        return await self.ai_scanner.analyze_code(prompt, "analysis", {})
5. 集成的主服务
python
# main_service.py
class DeepCodeAnalysisService:
    """深度代码分析服务"""
    
    def __init__(self):
        self.ai_scanner = EnhancedAICodeScanner()
        self.ai_chain_analyzer = AIChainAnalyzer(self.ai_scanner)
        self.issue_analyzer = DeepIssueAnalyzer()
    
    async def analyze_code_deeply(self, file_path: str, function_name: str, repo_root: str) -> Dict:
        """深度代码分析入口"""
        
        try:
            # 1. 调用链分析
            chain_analysis = await self.ai_chain_analyzer.deep_analyze_with_ai(
                file_path, function_name, repo_root
            )
            
            # 2. 问题检测
            rule_based_issues = await self.issue_analyzer.analyze_chain_issues(
                chain_analysis['call_chain'], repo_root
            )
            
            # 3. 结果整合
            combined_issues = self._combine_analysis_results(
                chain_analysis['validated_issues'],
                rule_based_issues
            )
            
            return {
                'success': True,
                'call_chain_metrics': chain_analysis['chain_metrics'],
                'issues': combined_issues,
                'analysis_summary': self._generate_summary(combined_issues),
                'recommendations': self._generate_recommendations(combined_issues)
            }
            
        except Exception as e:
            return {
                'success': False,
                'error': str(e),
                'issues': [],
                'analysis_summary': '分析失败'
            }
    
    def _generate_summary(self, issues: List[Dict]) -> Dict:
        """生成分析摘要"""
        
        severity_count = defaultdict(int)
        type_count = defaultdict(int)
        
        for issue in issues:
            severity_count[issue.get('severity', 'UNKNOWN')] += 1
            type_count[issue.get('type', 'OTHER')] += 1
        
        return {
            'total_issues': len(issues),
            'by_severity': dict(severity_count),
            'by_type': dict(type_count),
            'critical_chain_issues': len([i for i in issues if i.get('impact_scope') == 'CHAIN' and i.get('severity') in ['CRITICAL', 'HIGH']])
        }

# 使用示例
async def main():
    service = DeepCodeAnalysisService()
    
    # 分析支付处理链路
    result = await service.analyze_code_deeply(
        file_path="src/services/payment.py",
        function_name="process_payment",
        repo_root="/path/to/ecommerce-project"
    )
    
    if result['success']:
        print(f"发现 {result['analysis_summary']['total_issues']} 个问题")
        print(f"关键链路问题: {result['analysis_summary']['critical_chain_issues']} 个")
        
        for issue in result['issues'][:5]:  # 显示前5个问题
            print(f"- [{issue['severity']}] {issue['title']}")

if __name__ == "__main__":
    asyncio.run(main())
6. 配置和依赖
python
# requirements.txt
# AST分析
astroid==2.15.0
esprima==4.0.1
javalang==0.13.0
tree-sitter==0.20.1

# 图分析
networkx==3.1
graphviz==0.20.1

# 异步处理
asyncio==3.4.3
aiofiles==23.2.0

# AI集成
openai==1.3.0
anthropic==0.7.2

# 工具库
click==8.1.0
rich==13.0.0
核心特性
完整调用链重建: 从入口函数开始，重建完整的调用关系图

跨文件分析: 支持import/require等跨文件调用解析

深度问题检测:

性能瓶颈链

安全风险传递

数据流一致性

资源泄漏路径

AI增强分析: 结合AI理解业务上下文和架构问题

优先级排序: 基于影响范围和严重程度智能排序问题

这套方案能够让AI真正理解代码的执行路径，发现单个代码片段分析无法识别的系统性问题和架构缺陷。
