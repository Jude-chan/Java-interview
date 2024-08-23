实例工厂的意思是获取对象实例的方法不是静态的，所以你需要首先 new 工厂类，再调用普通的
实例方法：
public class DaoFactory { //实例工厂
public FactoryDao getFactoryDaoImpl(){
return new FactoryDaoImpl();
13/04/2018  Page 128 of 283
}
}
public class SpringAction {
private FactoryDao factoryDao; //注入对象
public void setFactoryDao(FactoryDao factoryDao) {
this.factoryDao = factoryDao;
}
}
<bean name="springAction" class="SpringAction">
<!--使用实例工厂的方法注入对象,对应下面的配置文件-->
<property name="factoryDao" ref="factoryDao"></property>
</bean>
<!--此处获取对象的方式是从工厂类中获取实例方法-->
<bean name="daoFactory" class="com.DaoFactory"></bean>
<bean name="factoryDao" factory-bean="daoFactory"
factory-method="getFactoryDaoImpl"></bean>
6.1.7.7.  5 种 种不同方式的自动装配 不同方式的自动装配
Spring 装配包括手动装配和自动装配，手动装配是有基于 xml 装配、构造方法、setter 方法等
自动装配有五种自动装配的方式，可以用来指导 Spring 容器用自动装配方式来进行依赖注入。
1.  no：默认的方式是不进行自动装配，通过显式设置 ref 属性来进行装配。
2.  byName：通过参数名 自动装配，Spring 容器在配置文件中发现 bean 的 autowire 属性被设
置成 byname，之后容器试图匹配、装配和该 bean 的属性具有相同名字的 bean。
3.  byType：通过参数类型自动装配，Spring 容器在配置文件中发现 bean 的 autowire 属性被
设置成byType，之后容器试图匹配、装配和该bean的属性具有相同类型的bean。如果有多
个 bean 符合条件，则抛出错误。
4.  constructor：这个方式类似于 byType， 但是要提供给构造器参数，如果没有确定的带参数
的构造器参数类型，将会抛出异常。
5.  autodetect：首先尝试使用 constructor 来自动装配，如果无法工作，则使用 byType方式。
13/04/2018  Page 129 of 283
6.1.8. Spring APO  原理
6.1.8.1.  概念 概念
" 横切"的技术，剖解开封装的对象内部，并将那些影响了多个类的公共行为封装到一个可重用模块，
并将其命名为"Aspect"，即切面。所谓"切面"，简单说就是那些与业务无关，却为业务模块所共
同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块之间的耦合度，并有利于未
来的可操作性和可维护性。
使用"横切"技术，AOP 把软件系统分为两个部分：核心关注点和横切关注点。业务处理的主要流
程是核心关注点，与之关系不大的部分是横切关注点。横切关注点的一个特点是，他们经常发生
在核心关注点的多处，而各处基本相似，比如权限认证、日志、事物。AOP 的作用在于分离系统
中的各种关注点，将核心关注点和横切关注点分离开来。
AOP 主要应用场景有：
1.  Authentication 权限
2.  Caching 缓存
3.  Context passing 内容传递
4.  Error handling 错误处理
5.  Lazy loading 懒加载
6.  Debugging 调试
7.  logging, tracing, profiling and monitoring 记录跟踪 优化 校准
8.  Performance optimization 性能优化
9.  Persistence 持久化
10. Resource pooling 资源池
11. Synchronization 同步
12. Transactions 事务
6.1.8.2.  AOP  核心概念
1、切面（aspect）：类是对物体特征的抽象，切面就是对横切关注点的抽象
2、横切关注点：对哪些方法进行拦截，拦截后怎么处理，这些关注点称之为横切关注点。
3、连接点（joinpoint）：被拦截到的点，因为 Spring 只支持方法类型的连接点，所以在 Spring
中连接点指的就是被拦截到的方法，实际上连接点还可以是字段或者构造器。
4、切入点（pointcut）：对连接点进行拦截的定义
5、通知（advice）：所谓通知指的就是指拦截到连接点之后要执行的代码，通知分为前置、后置、
异常、最终、环绕通知五类。
6、目标对象：代理的目标对象
7、织入（weave）：将切面应用到目标对象并导致代理对象创建的过程
13/04/2018  Page 130 of 283
8、引入（introduction）：在不修改代码的前提下，引入可以在运行期为类动态地添加一些方法
或字段。
参考：https://segmentfault.com/a/1190000007469968
6.1.8.1.  AOP  两种代理方式
Spring 提供了两种方式来生成代理对象: JDKProxy 和 Cglib，具体使用哪种方式生成由
AopProxyFactory 根据 AdvisedSupport 对象的配置来决定。默认的策略是如果目标类是接口，
则使用 JDK 动态代理技术，否则使用 Cglib 来生成代理。
JDK 动态 接口 代理
1.  JDK 动态代理主要涉及到 java.lang.reflect 包中的两个类：Proxy 和 InvocationHandler。
InvocationHandler是一个接口，通过实现该接口定义横切逻辑，并通过反射机制调用目标类
的代码，动态将横切逻辑和业务逻辑编制在一起。Proxy 利用 InvocationHandler 动态创建
一个符合某一接口的实例，生成目标类的代理对象。
13/04/2018  Page 131 of 283
CGLib 动态代理
2.  ：CGLib 全称为 Code Generation Library，是一个强大的高性能，高质量的代码生成类库，
可以在运行期扩展 Java 类与实现 Java 接口，CGLib 封装了 asm，可以再运行期动态生成新
的 class。和 JDK 动态代理相比较：JDK 创建代理有一个限制，就是只能为接口创建代理实例，
而对于没有通过接口定义业务方法的类，则可以通过 CGLib 创建动态代理。
6.1.8.2.  实现原理 实现原理
@Aspect
public class TransactionDemo {
@Pointcut(value="execution(* com.yangxin.core.service.*.*.*(..))")
public void point(){
}
@Before(value="point()")
public void before(){
System.out.println("transaction begin");
}
@AfterReturning(value = "point()")
public void after(){
System.out.println("transaction commit");
}
@Around("point()")
public void around(ProceedingJoinPoint joinPoint) throws Throwable{
System.out.println("transaction begin");
joinPoint.proceed();
System.out.println("transaction commit");
}
}
13/04/2018  Page 132 of 283
6.1.9. Spring MVC  原理
Spring 的模型-视图-控制器（MVC）框架是围绕一个 DispatcherServlet 来设计的，这个 Servlet
会把请求分发给各个处理器，并支持可配置的处理器映射、视图渲染、本地化、时区与主题渲染
等，甚至还能支持文件上传。
6.1.9.1.  MVC  流程
13/04/2018  Page 133 of 283
Http 请求 到 DispatcherServlet
(1) 客户端请求提交到 DispatcherServlet。
HandlerMapping 寻找处理器
(2) 由 DispatcherServlet 控制器查询一个或多个 HandlerMapping，找到处理请求的
Controller。
调用处理器 Controller
(3) DispatcherServlet 将请求提交到 Controller。
Controller 调用业务逻辑处理后，返回 ModelAndView
(4)(5)调用业务处理和返回结果：Controller 调用业务逻辑处理后，返回 ModelAndView。
DispatcherServlet 查询 ModelAndView
(6)(7)处理视图映射并返回模型： DispatcherServlet 查询一个或多个 ViewResoler 视图解析器，
找到 ModelAndView 指定的视图。
ModelAndView 反馈浏览器 HTTP
(8) Http 响应：视图负责将结果显示到客户端。
6.1.9.1.  MVC  常用注解
13/04/2018  Page 134 of 283
6.1.10.  Spring Boot  原理
Spring Boot 是由 Pivotal 团队提供的全新框架，其设计目的是用来简化新 Spring 应用的初始搭
建以及开发过程。该框架使用了特定的方式来进行配置，从而使开发人员不再需要定义样板化的
配置。通过这种方式，Spring Boot 致力于在蓬勃发展的快速应用开发领域(rapid application
development)成为领导者。其特点如下：
1.  创建独立的 Spring  应用程序
2.  嵌入的 Tomcat ，无需部署 WAR  文件
3.  简化 Maven  配置
4.  自动配置 Spring
5.  提供生产就绪型功能，如指标，健康检查和外部配置
6.  绝对没有代码生成和对 XML  没有要求配置 [1]
6.1.11.  JPA  原理
6.1.11.1.  事务
事务是计算机应用中不可或缺的组件模型，它保证了用户操作的原子性 ( Atomicity )、一致性
( Consistency )、隔离性 ( Isolation ) 和持久性 ( Durabilily )。
6.1.11.2.  本地事务
紧密依赖于底层资源管理器（例如数据库连接 )，事务处理局限在当前事务资源内。此种事务处理
方式不存在对应用服务器的依赖，因而部署灵活却无法支持多数据源的分布式事务。在数据库连
接中使用本地事务示例如下：
public void transferAccount() {
Connection conn = null;
Statement stmt = null;
try{
conn = getDataSource().getConnection();
// 将自动提交设置为 false，若设置为 true 则数据库将会把每一次数据更新认定为一个事务并自动提交
conn.setAutoCommit(false);
stmt = conn.createStatement();
// 将 A 账户中的金额减少 500
stmt.execute("update t_account set amount = amount - 500 where account_id = 'A'");
13/04/2018  Page 135 of 283
// 将 B 账户中的金额增加 500
stmt.execute("update t_account set amount = amount + 500 where account_id = 'B'");
// 提交事务
conn.commit();
// 事务提交：转账的两步操作同时成功
} catch(SQLException sqle){
// 发生异常，回滚在本事务中的操做
conn.rollback();
// 事务回滚：转账的两步操作完全撤销
stmt.close();
conn.close();
}
}
6.1.11.1. 分布式 分布式事务 事务
Java 事务编程接口（JTA：Java Transaction API）和 Java 事务服务 (JTS；Java Transaction
Service) 为 J2EE 平台提供了分布式事务服务。分布式事务（Distributed Transaction）包括事务
管理器（Transaction Manager）和一个或多个支持 XA 协议的资源管理器 ( Resource
Manager )。我们可以将资源管理器看做任意类型的持久化数据存储；事务管理器承担着所有事务
参与单元的协调与控制。
public void transferAccount() {
UserTransaction userTx = null;
Connection connA = null; Statement stmtA = null;
Connection connB = null; Statement stmtB = null;
try{
// 获得 Transaction 管理对象
userTx = (UserTransaction)getContext().lookup("java:comp/UserTransaction");
connA = getDataSourceA().getConnection();// 从数据库 A 中取得数据库连接
connB = getDataSourceB().getConnection();// 从数据库 B 中取得数据库连接
userTx.begin(); // 启动事务
stmtA = connA.createStatement();// 将 A 账户中的金额减少 500
stmtA.execute("update t_account set amount = amount - 500 where account_id = 'A'");
// 将 B 账户中的金额增加 500
stmtB = connB.createStatement();
13/04/2018  Page 136 of 283
stmtB.execute("update t_account set amount = amount + 500 where account_id = 'B'");
userTx.commit();// 提交事务
// 事务提交：转账的两步操作同时成功（数据库 A 和数据库 B 中的数据被同时更新）
} catch(SQLException sqle){
// 发生异常，回滚在本事务中的操纵
userTx.rollback();// 事务回滚：数据库 A 和数据库 B 中的数据更新被同时撤销
} catch(Exception ne){ }
}
6.1.11.1.  两阶段提交
两阶段提交主要保证了分布式事务的原子性：即所有结点要么全做要么全不做，所谓的两个阶段
是指：第一阶段：准备阶段；第二阶段：提交阶段。
1 准备阶段
事务协调者(事务管理器)给每个参与者(资源管理器)发送 Prepare 消息，每个参与者要么直接返回
失败(如权限验证失败)，要么在本地执行事务，写本地的 redo 和 undo 日志，但不提交，到达一
种“万事俱备，只欠东风”的状态。
2 提交阶段 ：
如果协调者收到了参与者的失败消息或者超时，直接给每个参与者发送回滚(Rollback)消息；否则，
发送提交(Commit)消息；参与者根据协调者的指令执行提交或者回滚操作，释放所有事务处理过
程中使用的锁资源。(注意:必须在最后阶段释放锁资源)
13/04/2018  Page 137 of 283
将提交分成两阶段进行的目的很明确，就是尽可能晚地提交事务，让事务在提交前尽可能地完成
所有能完成的工作。
6.1.12.  Mybatis  缓存
Mybatis中有一级缓存和二级缓存，默认情况下一级缓存是开启的，而且是不能关闭的。一级缓存
是指 SqlSession 级别的缓存，当在同一个 SqlSession 中进行相同的 SQL 语句查询时，第二次以
后的查询不会从数据库查询，而是直接从缓存中获取，一级缓存最多缓存 1024 条 SQL。二级缓存
是指可以跨 SqlSession 的缓存。是 mapper 级别的缓存，对于 mapper 级别的缓存不同的
sqlsession 是可以共享的。
13/04/2018  Page 138 of 283
6.1.12.1. Mybatis 的一级缓存原理 的一级缓存原理 （ sqlsession 级别 ） ）
第一次发出一个查询 sql，sql 查询结果写入 sqlsession 的一级缓存中，缓存使用的数据结构是一
个 map。
key：MapperID+offset+limit+Sql+所有的入参
value：用户信息
同一个 sqlsession 再次发出相同的 sql，就从缓存中取出数据。如果两次中间出现 commit 操作
（修改、添加、删除），本 sqlsession 中的一级缓存区域全部清空，下次再去缓存中查询不到所
以要从数据库查询，从数据库查询到再写入缓存。
6.1.12.2. 二级缓存原理 二级缓存原理 （ mapper 基本 ） ）
二级缓存的范围是 mapper 级别（mapper同一个命名空间），mapper 以命名空间为单位创建缓
存数据结构，结构是 map。mybatis 的二级缓存是通过 CacheExecutor 实现的。CacheExecutor
13/04/2018  Page 139 of 283
其实是 Executor 的代理对象。所有的查询操作，在 CacheExecutor 中都会先匹配缓存中是否存
在，不存在则查询数据库。
key：MapperID+offset+limit+Sql+所有的入参
具体使用需要配置：
1.  Mybatis 全局配置中启用二级缓存配置
2.  在对应的 Mapper.xml 中配置 cache 节点
3.  在对应的 select 查询节点中添加 useCache=true
6.1.13.  Tomcat  架构
http://www.importnew.com/21112.html
13/04/2018  Page 140 of 283
7. 微服务
7.1.1. 服务 服务 注册发现 发现
服务注册就是维护一个登记簿，它管理系统内所有的服务地址。当新的服务启动后，它会向登记
簿交待自己的地址信息。服务的依赖方直接向登记簿要Service Provider地址就行了。当下用于服
务注册的工具非常多 ZooKeeper，Consul，Etcd, 还有 Netflix 家的 eureka 等。服务注册有两种
形式：客户端注册和第三方注册。
7.1.1.1.  客户端注册（ （zookeeper ）
客户端注册是服务自身要负责注册与注销的工作。当服务启动后向注册中心注册自身，当服务下
线时注销自己。期间还需要和注册中心保持心跳。心跳不一定要客户端来做，也可以由注册中心
负责（这个过程叫探活）。这种方式的缺点是注册工作与服务耦合在一起，不同语言都要实现一
套注册逻辑。
7.1.1.2.  第三方注册 （ 独立的服务 Registrar ） ）
第三方注册由一个独立的服务Registrar负责注册与注销。当服务启动后以某种方式通知Registrar，
然后 Registrar 负责向注册中心发起注册工作。同时注册中心要维护与服务之间的心跳，当服务不
可用时，向注册中心注销服务。这种方式的缺点是 Registrar 必须是一个高可用的系统，否则注册
工作没法进展。
13/04/2018  Page 141 of 283
7.1.1.3.  客户端发现 客户端发现
客户端发现是指客户端负责查询可用服务地址，以及负载均衡的工作。这种方式最方便直接，而
且也方便做负载均衡。再者一旦发现某个服务不可用立即换另外一个，非常直接。缺点也在于多
语言时的重复工作，每个语言实现相同的逻辑。
13/04/2018  Page 142 of 283
7.1.1.4.  服务端发现 服务端发现
服务端发现需要额外的 Router 服务，请求先打到 Router，然后 Router 负责查询服务与负载均衡。
这种方式虽然没有客户端发现的缺点，但是它的缺点是保证 Router 的高可用。
7.1.1.5.  Consul
7.1.1.6.  Eureka
7.1.1.7.  SmartStack
7.1.1.8.  Etcd
7.1.2. API  网关
API Gateway 是一个服务器，也可以说是进入系统的唯一节点。这跟面向对象设计模式中的
Facade 模式很像。API Gateway 封装内部系统的架构，并且提供 API 给各个客户端。它还可能有
其他功能，如授权、监控、负载均衡、缓存、请求分片和管理、静态响应处理等。下图展示了一
个适应当前架构的 API Gateway。
13/04/2018  Page 143 of 283
API Gateway 负责请求转发、合成和协议转换。所有来自客户端的请求都要先经过 API Gateway，
然后路由这些请求到对应的微服务。API Gateway 将经常通过调用多个微服务来处理一个请求以
及聚合多个服务的结果。它可以在 web 协议与内部使用的非 Web 友好型协议间进行转换，如
HTTP 协议、WebSocket 协议。
7.1.2.1.  请求转发 请求转发
服务转发主要是对客户端的请求安装微服务的负载转发到不同的服务上
7.1.2.2.  响应合并 响应合并
把业务上需要调用多个服务接口才能完成的工作合并成一次调用对外统一提供服务。
7.1.2.3.  协议转换 协议转换
重点是支持 SOAP，JMS，Rest 间的协议转换。
7.1.2.4.  数据转换 数据转换
重点是支持 XML 和 Json 之间的报文格式转换能力（可选）
13/04/2018  Page 144 of 283
7.1.2.5.  安全认证 安全认证
1.  基于 Token 的客户端访问控制和安全策略
2.  传输数据和报文加密，到服务端解密，需要在客户端有独立的 SDK 代理包
3.  基于 Https 的传输加密，客户端和服务端数字证书支持
4.  基于 OAuth2.0 的服务安全认证(授权码，客户端，密码模式等）
7.1.3.  配置中心
配置中心一般用作系统的参数配置，它需要满足如下几个要求：高效获取、实时感知、分布式访
问。
7.1.3.1.  zookeeper  配置中心
实现的架构图如下所示，采取数据加载到内存方式解决高效获取的问题，借助 zookeeper 的节点
监听机制来实现实时感知。
7.1.3.2.  配置中心数据分类 配置中心数据分类
7.1.4. 事件调度 事件调度（ （kafka ）
消息服务和事件的统一调度，常用用 kafka ，activemq 等。
7.1.5. 服务跟踪 服务跟踪 （ starter-sleuth ） ）
随着微服务数量不断增长，需要跟踪一个请求从一个微服务到下一个微服务的传播过程， Spring
Cloud Sleuth 正是解决这个问题，它在日志中引入唯一 ID，以保证微服务调用之间的一致性，这
样你就能跟踪某个请求是如何从一个微服务传递到下一个。
13/04/2018  Page 145 of 283
1.  为了实现请求跟踪，当请求发送到分布式系统的入口端点时，只需要服务跟踪框架为该请求
创建一个唯一的跟踪标识，同时在分布式系统内部流转的时候，框架始终保持传递该唯一标
识，直到返回给请求方为止，这个唯一标识就是前文中提到的 Trace ID。通过 Trace ID 的记
录，我们就能将所有请求过程日志关联起来。
2.  为了统计各处理单元的时间延迟，当请求达到各个服务组件时，或是处理逻辑到达某个状态
时，也通过一个唯一标识来标记它的开始、具体过程以及结束，该标识就是我们前文中提到
的 Span ID，对于每个 Span 来说，它必须有开始和结束两个节点，通过记录开始 Span 和结
束 Span 的时间戳，就能统计出该 Span 的时间延迟，除了时间戳记录之外，它还可以包含一
些其他元数据，比如：事件名称、请求信息等。
3.  在快速入门示例中，我们轻松实现了日志级别的跟踪信息接入，这完全归功于spring-cloud-
starter-sleuth 组件的实现。在 Spring Boot 应用中，通过在工程中引入 spring-cloud-
starter-sleuth 依赖之后， 它会自动的为当前应用构建起各通信通道的跟踪机制，比如：
  通过诸如 RabbitMQ、Kafka（或者其他任何 Spring Cloud Stream 绑定器实现的消息
中间件）传递的请求。
  通过 Zuul 代理传递的请求。
  通过 RestTemplate 发起的请求。
7.1.6. 服务熔断 服务熔断（ （Hystrix ）
在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个
系统不可用的情况，这种现象被称为服务雪崩效应。服务雪崩效应是一种因“服务提供者”的不
可用导致“服务消费者”的不可用,并将不可用逐渐放大的过程。
熔断器的原理很简单，如同电力过载保护器。它可以实现快速失败，如果它在一段时间内侦测到
许多类似的错误，会强迫其以后的多个调用快速失败，不再访问远程服务器，从而防止应用程序
不断地尝试执行可能会失败的操作，使得应用程序继续执行而不用等待修正错误，或者浪费 CPU
时间去等到长时间的超时产生。熔断器也可以使应用程序能够诊断错误是否已经修正，如果已经
修正，应用程序会再次尝试调用操作。
13/04/2018  Page 146 of 283
7.1.6.1.  Hystrix  断路器机制
断路器很好理解, 当 Hystrix Command 请求后端服务失败数量超过一定比例(默认 50%), 断路器会
切换到开路状态(Open). 这时所有请求会直接失败而不会发送到后端服务. 断路器保持在开路状态
一段时间后(默认 5 秒), 自动切换到半开路状态(HALF-OPEN). 这时会判断下一次请求的返回情况,
如果请求成功, 断路器切回闭路状态(CLOSED), 否则重新切换到开路状态(OPEN). Hystrix 的断路器
就像我们家庭电路中的保险丝, 一旦后端服务不可用, 断路器会直接切断请求链, 避免发送大量无效
请求影响系统吞吐量, 并且断路器有自我检测并恢复的能力。
7.1.7. API  管理
SwaggerAPI 管理工具。
13/04/2018  Page 147 of 283
8. Netty 与 与 RPC
8.1.1. Netty  原理
Netty 是一个高性能、异步事件驱动的 NIO 框架，基于 JAVA NIO 提供的 API 实现。它提供了对
TCP、UDP 和文件传输的支持，作为一个异步 NIO 框架，Netty 的所有 IO 操作都是异步非阻塞
的，通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 IO 操作结果。
8.1.2. Netty  高性能
在IO编程过程中，当需要同时处理多个客户端接入请求时，可以利用多线程或者IO多路复用技术
进行处理。IO多路复用技术通过把多个 IO的阻塞复用到同一个 select 的阻塞上，从而使得系统在
单线程的情况下可以同时处理多个客户端请求。与传统的多线程/多进程模型比，I/O 多路复用的
最大优势是系统开销小，系统不需要创建新的额外进程或者线程，也不需要维护这些进程和线程
的运行，降低了系统的维护工作量，节省了系统资源。
与Socket类和ServerSocket类相对应，NIO也提供了SocketChannel和ServerSocketChannel
两种不同的套接字通道实现。
8.1.2.1.  多路复用 通讯方式
Netty 架构按照 Reactor 模式设计和实现，它的服务端通信序列图如下：
客户端通信序列图如下：
13/04/2018  Page 148 of 283
Netty 的 IO 线程 NioEventLoop 由于聚合了多路复用器 Selector，可以同时并发处理成百上千个
客户端 Channel，由于读写操作都是非阻塞的，这就可以充分提升 IO 线程的运行效率，避免由于
频繁 IO 阻塞导致的线程挂起。
8.1.2.1.  异步通讯 异步通讯 NIO
由于 Netty 采用了异步通信模式，一个 IO 线程可以并发处理 N 个客户端连接和读写操作，这从根
本上解决了传统同步阻塞 IO 一连接一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极
大的提升。
13/04/2018  Page 149 of 283
8.1.2.2.  零拷贝（ （DIRECT BUFFERS 使用堆外直接内存 使用堆外直接内存） ）
1.  Netty 的接收和发送 ByteBuffer 采用 DIRECT BUFFERS，使用堆外直接内存进行 Socket 读写，
不需要进行字节缓冲区的二次拷贝。如果使用传统的堆内存（HEAP BUFFERS）进行 Socket 读写，
JVM 会将堆内存 Buffer 拷贝一份到直接内存中，然后才写入 Socket 中。相比于堆外直接内存，
消息在发送过程中多了一次缓冲区的内存拷贝。
2.  Netty 提供了组合 Buffer 对象，可以聚合多个 ByteBuffer 对象，用户可以像操作一个 Buffer 那样
方便的对组合 Buffer 进行操作，避免了传统通过内存拷贝的方式将几个小 Buffer 合并成一个大的
Buffer。
3.  Netty的文件传输采用了transferTo方法，它可以直接将文件缓冲区的数据发送到目标Channel，
避免了传统通过循环 write 方式导致的内存拷贝问题
8.1.2.3.  内存池 （ 基于内存池的缓冲区重用机制） ）
随着 JVM 虚拟机和 JIT 即时编译技术的发展，对象的分配和回收是个非常轻量级的工作。但是对于缓
冲区 Buffer，情况却稍有不同，特别是对于堆外直接内存的分配和回收，是一件耗时的操作。为了尽
量重用缓冲区，Netty 提供了基于内存池的缓冲区重用机制。
8.1.2.4.  高效的 高效的 Reactor  线程模型
常用的 Reactor 线程模型有三种，Reactor 单线程模型, Reactor 多线程模型, 主从 Reactor 多线程模
型。
Reactor 单线程模型
Reactor 单线程模型，指的是所有的 IO 操作都在同一个 NIO 线程上面完成，NIO 线程的职责如下：
1) 作为 NIO 服务端，接收客户端的 TCP 连接；
2) 作为 NIO 客户端，向服务端发起 TCP 连接；
3) 读取通信对端的请求或者应答消息；
4) 向通信对端发送消息请求或者应答消息。
13/04/2018  Page 150 of 283
由于 Reactor 模式使用的是异步非阻塞 IO，所有的 IO 操作都不会导致阻塞，理论上一个线程可以独
立处理所有 IO 相关的操作。从架构层面看，一个 NIO 线程确实可以完成其承担的职责。例如，通过
Acceptor 接收客户端的 TCP 连接请求消息，链路建立成功之后，通过 Dispatch 将对应的 ByteBuffer
派发到指定的 Handler 上进行消息解码。用户 Handler 可以通过 NIO 线程将消息发送给客户端。
Reactor 多线程模型
Rector 多线程模型与单线程模型最大的区别就是有一组 NIO 线程处理 IO 操作。 有专门一个
NIO 线程-Acceptor 线程用于监听服务端，接收客户端的 TCP 连接请求； 网络 IO 操作-读、写
等由一个 NIO 线程池负责，线程池可以采用标准的 JDK 线程池实现，它包含一个任务队列和 N
个可用的线程，由这些 NIO 线程负责消息的读取、解码、编码和发送；
主从 Reactor 多线程模型
服务端用于接收客户端连接的不再是个 1 个单独的 NIO 线程，而是一个独立的 NIO 线程池。
Acceptor 接收到客户端 TCP 连接请求处理完成后（可能包含接入认证等），将新创建的
SocketChannel 注册到 IO 线程池（sub reactor 线程池）的某个 IO 线程上，由它负责
SocketChannel 的读写和编解码工作。Acceptor 线程池仅仅只用于客户端的登陆、握手和安全
认证，一旦链路建立成功，就将链路注册到后端 subReactor 线程池的 IO 线程上，由 IO 线程负
责后续的 IO 操作。
13/04/2018  Page 151 of 283
8.1.2.5.  无锁设计 、线程绑定 绑定
Netty 采用了串行无锁化设计，在 IO 线程内部进行串行操作，避免多线程竞争导致的性能下降。
表面上看，串行化设计似乎 CPU 利用率不高，并发程度不够。但是，通过调整 NIO 线程池的线程
参数，可以同时启动多个串行化的线程并行运行，这种局部无锁化的串行线程设计相比一个队列-
多个工作线程模型性能更优。
Netty 的 NioEventLoop 读取到消息之后，直接调用 ChannelPipeline 的
fireChannelRead(Object msg)，只要用户不主动切换线程，一直会由 NioEventLoop 调用
到用户的 Handler，期间不进行线程切换，这种串行化处理方式避免了多线程操作导致的锁
的竞争，从性能角度看是最优的。
8.1.2.6.  高性能的序列化框架 高性能的序列化框架
Netty 默认提供了对 Google Protobuf 的支持，通过扩展 Netty 的编解码接口，用户可以实现其它的
高性能序列化框架，例如 Thrift 的压缩二进制编解码框架。
1.  SO_RCVBUF 和 SO_SNDBUF：通常建议值为 128K 或者 256K。
13/04/2018  Page 152 of 283
小包封大包，防止网络阻塞
2.  SO_TCPNODELAY：NAGLE 算法通过将缓冲区内的小封包自动相连，组成较大的封包，阻止大量
小封包的发送阻塞网络，从而提高网络应用效率。但是对于时延敏感的应用场景需要关闭该优化算
法。
软中断 Hash 值和 CPU 绑定
3.  软中断：开启 RPS 后可以实现软中断，提升网络吞吐量。RPS 根据数据包的源地址，目的地址以
及目的和源端口，计算出一个 hash 值，然后根据这个 hash 值来选择软中断运行的 cpu，从上层
来看，也就是说将每个连接和 cpu 绑定，并通过这个 hash 值，来均衡软中断在多个 cpu 上，提升
网络并行处理性能。
8.1.3. Netty RPC  实现
8.1.3.1.  概念 概念
RPC，即 Remote Procedure Call（远程过程调用），调用远程计算机上的服务，就像调用本地服务一
样。RPC 可以很好的解耦系统，如 WebService 就是一种基于 Http 协议的 RPC。这个 RPC 整体框架
如下：
8.1.3.2.  关键技术 关键技术
1.  服务发布与订阅：服务端使用 Zookeeper 注册服务地址，客户端从 Zookeeper 获取可用的服务
地址。
2.  通信：使用 Netty 作为通信框架。
3.  Spring：使用 Spring 配置服务，加载 Bean，扫描注解。
4.  动态代理：客户端使用代理模式透明化服务调用。
5.  消息编解码：使用 Protostuff 序列化和反序列化消息。
8.1.3.3.  核心流程 核心流程
1.  服务消费方（client）调用以本地调用方式调用服务；
13/04/2018  Page 153 of 283
2.  client stub 接收到调用后负责将方法、参数等组装成能够进行网络传输的消息体；
3.  client stub 找到服务地址，并将消息发送到服务端；
4.  server stub 收到消息后进行解码；
5.  server stub 根据解码结果调用本地的服务；
6.  本地服务执行并将结果返回给 server stub；
7.  server stub 将返回结果打包成消息并发送至消费方；
8.  client stub 接收到消息，并进行解码；
9.  服务消费方得到最终结果。
RPC 的目标就是要 2~8 这些步骤都封装起来，让用户对这些细节透明。JAVA 一般使用动态代
理方式实现远程调用。
8.1.3.1.  消息编解码 消息编解码
息数据结构 （接口名称 + 方法名 + 参数类型和参数值 + 超时时间 + requestID ）
客户端的请求消息结构一般需要包括以下内容：
1.  接口名称：在我们的例子里接口名是“HelloWorldService”，如果不传，服务端就不知道调用哪
个接口了；
2.  方法名：一个接口内可能有很多方法，如果不传方法名服务端也就不知道调用哪个方法；
3.  参数类型和参数值：参数类型有很多，比如有 bool、int、long、double、string、map、list，
甚至如 struct（class）；以及相应的参数值；
4.  超时时间：
5.  requestID，标识唯一请求 id，在下面一节会详细描述 requestID 的用处。
6.  服务端返回的消息 ： 一般包括以下内容。返回值+状态 code+requestID
13/04/2018  Page 154 of 283
序列化
目前互联网公司广泛使用 Protobuf、Thrift、Avro 等成熟的序列化解决方案来搭建 RPC 框架，这
些都是久经考验的解决方案。
8.1.3.1.  通讯过程 通讯过程
核心问题 ( 线程暂停、 消息乱序 )
如果使用 netty 的话，一般会用 channel.writeAndFlush()方法来发送消息二进制串，这个方
法调用后对于整个远程调用(从发出请求到接收到结果)来说是一个异步的，即对于当前线程来说，
将请求发送出来后，线程就可以往后执行了，至于服务端的结果，是服务端处理完成后，再以消息
的形式发送给客户端的。于是这里出现以下两个问题：
1.  怎么让当前线程“暂停”，等结果回来后，再向后执行？
2.  如果有多个线程同时进行远程方法调用，这时建立在 client server 之间的 socket 连接上
会有很多双方发送的消息传递，前后顺序也可能是随机的，server 处理完结果后，将结
果消息发送给 client，client 收到很多消息，怎么知道哪个消息结果是原先哪个线程调用
的？如下图所示，线程 A 和线程 B 同时向 client socket 发送请求 requestA 和 requestB，
socket 先后将 requestB 和 requestA 发送至 server，而 server 可能将 responseB 先返
回，尽管 requestB 请求到达时间更晚。我们需要一种机制保证 responseA 丢给
ThreadA，responseB 丢给 ThreadB。
通讯流程
requestID 生成 -AtomicLong
1.  client 线程每次通过 socket 调用一次远程接口前，生成一个唯一的 ID，即 requestID
（requestID 必需保证在一个 Socket 连接里面是唯一的），一般常常使用 AtomicLong
从 0 开始累计数字生成唯一 ID；
存放回调对象 callback 到全局 ConcurrentHashMap
2.  将 处 理 结 果 的 回 调 对 象 callback ， 存 放 到 全 局 ConcurrentHashMap 里 面
put(requestID, callback)；
synchronized 获取回调对象 callback 的锁 并 自旋 wait
3.  当线程调用 channel.writeAndFlush()发送消息后，紧接着执行 callback 的 get()方法试
图获取远程返回的结果。在 get()内部，则使用 synchronized 获取回调对象 callback 的
锁，再先检测是否已经获取到结果，如果没有，然后调用 callback 的 wait()方法，释放
callback 上的锁，让当前线程处于等待状态。
13/04/2018  Page 155 of 283
监听消息的线程收到消息，找到 callback 上的锁并唤醒
4.  服务端接收到请求并处理后，将response结果（此结果中包含了前面的requestID）发
送给客户端，客户端 socket 连接上专门监听消息的线程收到消息，分析结果，取到
requestID，再从前面的 ConcurrentHashMap 里面 get(requestID)，从而找到
callback 对象，再用 synchronized 获取 callback 上的锁，将方法调用结果设置到
callback 对象里，再调用 callback.notifyAll()唤醒前面处于等待状态的线程。
public Object get() {
synchronized (this) { // 旋锁
while (true) { // 是否有结果了
If （!isDone）{
wait(); //没结果释放锁，让当前线程处于等待状态
}else{//获取数据并处理
}
}
}
}
private void setDone(Response res) {
this.res = res;
isDone = true;
synchronized (this) { //获取锁，因为前面 wait()已经释放了 callback 的锁了
notifyAll(); // 唤醒处于等待的线程
}
}
8.1.4. RMI 实现 实现方式 方式
Java 远程方法调用，即 Java RMI（Java Remote Method Invocation）是 Java 编程语言里，一种用
于实现远程过程调用的应用程序编程接口。它使客户机上运行的程序可以调用远程服务器上的对象。远
程方法调用特性使 Java 编程人员能够在网络环境中分布操作。RMI 全部的宗旨就是尽可能简化远程接
口对象的使用。
8.1.4.1.  实现 实现步骤 步骤
1.  编写远程服务接口，该接口必须继承 java.rmi.Remote 接口，方法必须抛出
java.rmi.RemoteException 异常；
2.  编写远程接口实现类，该实现类必须继承 java.rmi.server.UnicastRemoteObject 类；
3.  运行 RMI 编译器（rmic），创建客户端 stub 类和服务端 skeleton 类;
4.  启动一个 RMI 注册表，以便驻留这些服务;
13/04/2018  Page 156 of 283
5.  在 RMI 注册表中注册服务；
6.  客户端查找远程对象，并调用远程方法；
1：创建远程接口，继承 java.rmi.Remote 接口
public interface GreetService extends java.rmi.Remote {
String sayHello(String name) throws RemoteException;
}
2：实现远程接口，继承 java.rmi.server.UnicastRemoteObject 类
public class GreetServiceImpl extends java.rmi.server.UnicastRemoteObject
implements GreetService {
private static final long serialVersionUID = 3434060152387200042L;
public GreetServiceImpl() throws RemoteException {
super();
}
@Override
public String sayHello(String name) throws RemoteException {
return "Hello " + name;
}
}
3：生成 Stub 和 Skeleton;
4：执行 rmiregistry 命令注册服务
5：启动服务
LocateRegistry.createRegistry(1098);
Naming.bind("rmi://10.108.1.138:1098/GreetService", new GreetServiceImpl());
6.客户端调用
GreetService  greetService  =  (GreetService)
Naming.lookup("rmi://10.108.1.138:1098/GreetService");
System.out.println(greetService.sayHello("Jobs"));
8.1.5. Protoclol Buffer
protocol buffer 是 google 的一个开源项目,它是用于结构化数据串行化的灵活、高效、自动的方法，
例如 XML，不过它比 xml 更小、更快、也更简单。你可以定义自己的数据结构，然后使用代码生成器
生成的代码来读写这个数据结构。你甚至可以在无需重新部署程序的情况下更新数据结构。
13/04/2018  Page 157 of 283
8.1.5.1.  特点 特点
Protocol Buffer 的序列化 & 反序列化简单 & 速度快的原因是：
1. 编码 / 解码 方式简单（只需要简单的数学运算 = 位移等等）
2. 采用 Protocol Buffer 自身的框架代码 和 编译器 共同完成
Protocol Buffer 的数据压缩效果好（即序列化后的数据量体积小）的原因是：
1. a. 采用了独特的编码方式，如 Varint、Zigzag 编码方式等等
2. b. 采用 T - L - V 的数据存储方式：减少了分隔符的使用 & 数据存储得紧凑
8.1.6. Thrift
Apache Thrift 是 Facebook 实现的一种高效的、支持多种编程语言的远程服务调用的框架。本文将从
Java 开发人员角度详细介绍 Apache Thrift 的架构、开发和部署，并且针对不同的传输协议和服务类
型给出相应的 Java 实例，同时详细介绍 Thrift 异步客户端的实现，最后提出使用 Thrift 需要注意的事
项。
目前流行的服务调用方式有很多种，例如基于 SOAP 消息格式的 Web Service，基于 JSON 消息格式
的 RESTful 服务等。其中所用到的数据传输方式包括 XML，JSON 等，然而 XML 相对体积太大，传输
效率低，JSON 体积较小，新颖，但还不够完善。本文将介绍由 Facebook 开发的远程服务调用框架
Apache Thrift，它采用接口描述语言定义并创建服务，支持可扩展的跨语言服务开发，所包含的代码
生成引擎可以在多种语言中，如 C++, Java, Python, PHP, Ruby, Erlang, Perl, Haskell, C#, Cocoa,
Smalltalk 等创建高效的、无缝的服务，其传输数据采用二进制格式，相对 XML 和 JSON 体积更小，
对于高并发、大数据量和多语言的环境更有优势。本文将详细介绍 Thrift 的使用，并且提供丰富的实例
代码加以解释说明，帮助使用者快速构建服务。
为什么要 Thrift：
1、多语言开发的需要 2、性能问题
13/04/2018  Page 158 of 283
13/04/2018  Page 159 of 283
9.  网络
9.1.1.  网络 7  层架构
7 层模型主要包括：
1.  物理层：主要定义物理设备标准，如网线的接口类型、光纤的接口类型、各种传输介质的传输速率
等。它的主要作用是传输比特流（就是由 1、0 转化为电流强弱来进行传输,到达目的地后在转化为
1、0，也就是我们常说的模数转换与数模转换）。这一层的数据叫做比特。
2.  数据链路层：主要将从物理层接收的数据进行 MAC 地址（网卡的地址）的封装与解封装。常把这
一层的数据叫做帧。在这一层工作的设备是交换机，数据通过交换机来传输。
3.  网络层：主要将从下层接收到的数据进行 IP 地址（例 192.168.0.1)的封装与解封装。在这一层工
作的设备是路由器，常把这一层的数据叫做数据包。
4.  传输层：定义了一些传输数据的协议和端口号（WWW 端口 80 等），如：TCP（传输控制协议，
传输效率低，可靠性强，用于传输可靠性要求高，数据量大的数据），UDP（用户数据报协议，
与 TCP 特性恰恰相反，用于传输可靠性要求不高，数据量小的数据，如 QQ 聊天数据就是通过这
种方式传输的）。 主要是将从下层接收的数据进行分段进行传输，到达目的地址后在进行重组。
常常把这一层数据叫做段。
5.  会话层：通过传输层（端口号：传输端口与接收端口）建立数据传输的通路。主要在你的系统之间
发起会话或或者接受会话请求（设备之间需要互相认识可以是 IP 也可以是 MAC 或者是主机名）
6.  表示层：主要是进行对接收的数据进行解释、加密与解密、压缩与解压缩等（也就是把计算机能够
识别的东西转换成人能够能识别的东西（如图片、声音等））
7.  应用层 主要是一些终端的应用，比如说FTP（各种文件下载），WEB（IE浏览），QQ之类的（你
就把它理解成我们在电脑屏幕上可以看到的东西．就 是终端应用）。
13/04/2018  Page 160 of 283
9.1.2. TCP/IP  原理
TCP/IP 协议不是 TCP 和 IP 这两个协议的合称，而是指因特网整个 TCP/IP 协议族。从协议分层
模型方面来讲，TCP/IP 由四个层次组成：网络接口层、网络层、传输层、应用层。
9.1.2.1.  网络访问层 访问层(Network Access Layer)
1.  网络访问层(Network Access Layer)在 TCP/IP 参考模型中并没有详细描述，只是指出主机
必须使用某种协议与网络相连。
9.1.2.2.  网络层 网络层(Internet Layer)
2.  网络层(Internet Layer)是整个体系结构的关键部分，其功能是使主机可以把分组发往任何网
络，并使分组独立地传向目标。这些分组可能经由不同的网络，到达的顺序和发送的顺序也
可能不同。高层如果需要顺序收发，那么就必须自行处理对分组的排序。互联网层使用因特
网协议(IP，Internet Protocol)。
9.1.2.3.  传输层 传输层(Tramsport Layer-TCP/UDP)
3.  传输层(Tramsport Layer)使源端和目的端机器上的对等实体可以进行会话。在这一层定义了
两个端到端的协议：传输控制协议(TCP，Transmission Control Protocol)和用户数据报协
议(UDP，User Datagram Protocol)。TCP 是面向连接的协议，它提供可靠的报文传输和对
上层应用的连接服务。为此，除了基本的数据传输外，它还有可靠性保证、流量控制、多路
复用、优先权和安全性控制等功能。UDP 是面向无连接的不可靠传输的协议，主要用于不需
要 TCP 的排序和流量控制等功能的应用程序。
9.1.2.4.  应用层 应用层(Application Layer)
4.  应用层(Application Layer)包含所有的高层协议，包括：虚拟终端协议(TELNET，
TELecommunications NETwork)、文件传输协议(FTP，File Transfer Protocol)、电子邮件
传输协议(SMTP，Simple Mail Transfer Protocol)、域名服务(DNS，Domain Name
13/04/2018  Page 161 of 283
Service)、网上新闻传输协议(NNTP，Net News Transfer Protocol)和超文本传送协议
(HTTP，HyperText Transfer Protocol)等。
9.1.3. TCP  三次握手/ 四次挥手
TCP 在传输之前会进行三次沟通，一般称为“三次握手”，传完数据断开的时候要进行四次沟通，一般
称为“四次挥手”。
9.1.3.1.  数据包说明 数据包说明
1.  源端口号（ 16 位）：它（连同源主机 IP 地址）标识源主机的一个应用进程。
2.  目的端口号（ 16 位）：它（连同目的主机 IP 地址）标识目的主机的一个应用进程。这两个值
加上 IP 报头中的源主机 IP 地址和目的主机 IP 地址唯一确定一个 TCP 连接。
3.  顺序号 seq（ 32 位）：用来标识从 TCP 源端向 TCP 目的端发送的数据字节流，它表示在这个
报文段中的第一个数据字节的顺序号。如果将字节流看作在两个应用程序间的单向流动，则
TCP 用顺序号对每个字节进行计数。序号是 32bit 的无符号数，序号到达 2 的 32 次方 － 1 后
又从 0 开始。当建立一个新的连接时， SYN 标志变 1 ，顺序号字段包含由这个主机选择的该
连接的初始顺序号 ISN （ Initial Sequence Number ）。
4.  确认号 ack（ 32 位）：包含发送确认的一端所期望收到的下一个顺序号。因此，确认序号应当
是上次已成功收到数据字节顺序号加 1 。只有 ACK 标志为 1 时确认序号字段才有效。 TCP 为
应用层提供全双工服务，这意味数据能在两个方向上独立地进行传输。因此，连接的每一端必
须保持每个方向上的传输数据顺序号。
5.  TCP 报头长度（ 4 位）：给出报头中 32bit 字的数目，它实际上指明数据从哪里开始。需要这
个值是因为任选字段的长度是可变的。这个字段占 4bit ，因此 TCP 最多有 60 字节的首部。然
而，没有任选字段，正常的长度是 20 字节。
6.  保留位（ 6 位）：保留给将来使用，目前必须置为 0 。
7.  控制位（ control flags ， 6 位）：在 TCP 报头中有 6 个标志比特，它们中的多个可同时被设
置为 1 。依次为：
  URG ：为 1 表示紧急指针有效，为 0 则忽略紧急指针值。
  ACK ：为 1 表示确认号有效，为 0 表示报文中不包含确认信息，忽略确认号字段。
  PSH ：为 1 表示是带有 PUSH 标志的数据，指示接收方应该尽快将这个报文段交给应用层
而不用等待缓冲区装满。
  RST ：用于复位由于主机崩溃或其他原因而出现错误的连接。它还可以用于拒绝非法的报
文段和拒绝连接请求。一般情况下，如果收到一个 RST 为 1 的报文，那么一定发生了某些
问题。
  SYN ：同步序号，为 1 表示连接请求，用于建立连接和使顺序号同步（ synchronize ）。
  FIN ：用于释放连接，为 1 表示发送方已经没有数据发送了，即关闭本方数据流。
8.  窗口大小（ 16 位）：数据字节数，表示从确认号开始，本报文的源方可以接收的字节数，即源
方接收窗口大小。窗口大小是一个 16bit 字段，因而窗口大小最大为 65535 字节。
9.  校验和（ 16 位）：此校验和是对整个的 TCP 报文段，包括 TCP 头部和 TCP 数据，以 16 位字
进行计算所得。这是一个强制性的字段，一定是由发送端计算和存储，并由接收端进行验证。
10. 紧急指针（ 16 位）：只有当 URG 标志置 1 时紧急指针才有效。TCP 的紧急方式是发送端向另
一端发送紧急数据的一种方式。
13/04/2018  Page 162 of 283
11. 选项：最常见的可选字段是最长报文大小，又称为 MSS(Maximum Segment Size) 。每个连
接方通常都在通信的第一个报文段（为建立连接而设置 SYN 标志的那个段）中指明这个选项，
它指明本端所能接收的最大长度的报文段。选项长度不一定是 32 位字的整数倍，所以要加填充
位，使得报头长度成为整字数。
12. 数据： TCP 报文段中的数据部分是可选的。在一个连接建立和一个连接终止时，双方交换的报
文段仅有 TCP 首部。如果一方没有数据要发送，也使用没有任何数据的首部来确认收到的数
据。在处理超时的许多情况中，也会发送不带任何数据的报文段。
9.1.3.2.  三次握手 三次握手
第一次握手：主机 A 发送位码为 syn＝1,随机产生 seq number=1234567 的数据包到服务器，主机 B
由 SYN=1 知道，A 要求建立联机；
第二次握手：主机 B 收到请求后要确认联机 信息，向 A 发 送 ack number=( 主机 A 的
seq+1),syn=1,ack=1,随机产生 seq=7654321 的包
第三次握手：主机 A 收到后检查 ack number 是否正确，即第一次发送的 seq number+1,以及位码
ack 是否为 1，若正确，主机 A 会再发送 ack number=(主机 B 的 seq+1),ack=1，主机 B 收到后确认
13/04/2018  Page 163 of 283
seq 值与 ack=1 则连接建立成功。
9.1.3.3.  四次挥手 四次挥手
TCP 建立连接要进行三次握手，而断开连接要进行四次。这是由于 TCP 的半关闭造成的。因为 TCP 连
接是全双工的(即数据可在两个方向上同时传递)所以进行关闭时每个方向上都要单独进行关闭。这个单
方向的关闭就叫半关闭。当一方完成它的数据发送任务，就发送一个 FIN 来向另一方通告将要终止这个
方向的连接。
1） 关闭客户端到服务器的连接：首先客户端 A 发送一个 FIN，用来关闭客户到服务器的数据传送，
然后等待服务器的确认。其中终止标志位 FIN=1，序列号 seq=u
2） 服务器收到这个 FIN，它发回一个 ACK，确认号 ack 为收到的序号加 1。
3） 关闭服务器到客户端的连接：也是发送一个 FIN 给客户端。
4） 客户段收到 FIN 后，并发回一个 ACK 报文确认，并将确认序号 seq 设置为收到序号加 1。
首先进行关闭的一方将执行主动关闭，而另一方执行被动关闭。
13/04/2018  Page 164 of 283
主机 A 发送 FIN 后，进入终止等待状态， 服务器 B 收到主机 A 连接释放报文段后，就立即
给主机 A 发送确认，然后服务器 B 就进入 close-wait 状态，此时 TCP 服务器进程就通知高
层应用进程，因而从 A 到 B 的连接就释放了。此时是“半关闭”状态。即 A 不可以发送给
B，但是 B 可以发送给 A。此时，若 B 没有数据报要发送给 A 了，其应用进程就通知 TCP 释
放连接，然后发送给 A 连接释放报文段，并等待确认。A 发送确认后，进入 time-wait，注
意，此时 TCP 连接还没有释放掉，然后经过时间等待计时器设置的 2MSL 后，A 才进入到
close 状态。
9.1.4. HTTP  原理
HTTP是一个无状态的协议。无状态是指客户机（Web浏览器）和服务器之间不需要建立持久的连接，
这意味着当一个客户端向服务器端发出请求，然后服务器返回响应(response)，连接就被关闭了，在服
务器端不保留连接的有关信息.HTTP 遵循请求(Request)/应答(Response)模型。客户机（浏览器）向
服务器发送请求，服务器处理请求并返回适当的应答。所有 HTTP 连接都被构造成一套请求和应答。
9.1.4.1.  传输流程 传输流程
1 ：地址解析
如用客户端浏览器请求这个页面：http://localhost.com:8080/index.htm 从中分解出协议名、主机名、
端口、对象路径等部分，对于我们的这个地址，解析得到的结果如下：
协议名：http
主机名：localhost.com
端口：8080
对象路径：/index.htm
13/04/2018  Page 165 of 283
在这一步，需要域名系统 DNS 解析域名 localhost.com,得主机的 IP 地址。
2 ： 封装 HTTP 请求数据包
把以上部分结合本机自己的信息，封装成一个 HTTP 请求数据包
3 ： 封装成 TCP 包并建立连接
封装成 TCP 包，建立 TCP 连接（TCP 的三次握手）
4 ： 客户机发送请求命
4）客户机发送请求命令：建立连接后，客户机发送一个请求给服务器，请求方式的格式为：统一资
源标识符（URL）、协议版本号，后边是 MIME 信息包括请求修饰符、客户机信息和可内容。
5 ： 服务器响应
服务器接到请求后，给予相应的响应信息，其格式为一个状态行，包括信息的协议版本号、一个成功或
错误的代码，后边是 MIME 信息包括服务器信息、实体信息和可能的内容。
6 ： 服务器关闭 TCP 连接
服务器关闭 TCP 连接：一般情况下，一旦 Web 服务器向浏览器发送了请求数据，它就要关闭 TCP 连
接，然后如果浏览器或者服务器在其头信息加入了这行代码 Connection:keep-alive，TCP 连接在发送
后将仍然保持打开状态，于是，浏览器可以继续通过相同的连接发送请求。保持连接节省了为每个请求
建立新连接所需的时间，还节约了网络带宽。
9.1.4.2.  HTTP  状态
状态码  原因短语
消息响应
100  Continue(继续)
101  Switching Protocol(切换协议)
13/04/2018  Page 166 of 283
成功响应
200  OK(成功)
201  Created(已创建)
202  Accepted(已创建)
203  Non-Authoritative Information(未授权信息)
204  No Content(无内容)
205  Reset Content(重置内容)
206  Partial Content(部分内容)
重定向
300  Multiple Choice(多种选择)
301  Moved Permanently(永久移动)
302  Found(临时移动)
303  See Other(查看其他位置)
304  Not Modified(未修改)
305  Use Proxy(使用代理)
306  unused (未使用)
307  Temporary Redirect(临时重定向)
308  Permanent Redirect(永久重定向)
客户端错误
400  Bad Request(错误请求)
401  Unauthorized(未授权)
402  Payment Required(需要付款)
403  Forbidden(禁止访问)
404  Not Found(未找到)
405  Method Not Allowed(不允许使用该方法)
406  Not Acceptable(无法接受)
407  Proxy Authentication Required(要求代理身份验证)
408  Request Timeout(请求超时)
409  Conflict(冲突)
410  Gone(已失效)
411  Length Required(需要内容长度头)
412  Precondition Failed(预处理失败)
413  Request Entity Too Large(请求实体过长)
414  Request-URI Too Long(请求网址过长)
415  Unsupported Media Type(媒体类型不支持)
416  Requested Range Not Satisfiable(请求范围不合要求)
417  Expectation Failed(预期结果失败)
服务器端错误
500  Internal Server Error(内部服务器错误)
501  Implemented(未实现)
502  Bad Gateway(网关错误)
503  Service Unavailable(服务不可用)
504  Gateway Timeout (网关超时)
505  HTTP Version Not Supported(HTTP 版本不受支持)
9.1.4.3.  HTTPS
HTTPS（全称：Hypertext Transfer Protocol over Secure Socket Layer），是以安全为目标的
HTTP 通道，简单讲是 HTTP 的安全版。即 HTTP 下加入 SSL 层，HTTPS 的安全基础是 SSL。其所用
的端口号是 443。 过程大致如下：
13/04/2018  Page 167 of 283
建立连接获取证书
1） SSL 客户端通过 TCP 和服务器建立连接之后（443 端口），并且在一般的 tcp 连接协商（握
手）过程中请求证书。即客户端发出一个消息给服务器，这个消息里面包含了自己可实现的算
法列表和其它一些需要的消息，SSL 的服务器端会回应一个数据包，这里面确定了这次通信所
需要的算法，然后服务器向客户端返回证书。（证书里面包含了服务器信息：域名。申请证书
的公司，公共秘钥）。
证书验证
2） Client 在收到服务器返回的证书后，判断签发这个证书的公共签发机构，并使用这个机构的公
共秘钥确认签名是否有效，客户端还会确保证书中列出的域名就是它正在连接的域名。
数据加密和传输
3） 如果确认证书有效，那么生成对称秘钥并使用服务器的公共秘钥进行加密。然后发送给服务
器，服务器使用它的私钥对它进行解密，这样两台计算机可以开始进行对称加密进行通信。
9.1.5. CDN  原理
CND 一般包含分发服务系统、负载均衡系统和管理系统
9.1.5.1.  分发服务系统 分发服务系统
其基本的工作单元就是各个 Cache 服务器。负责直接响应用户请求，将内容快速分发到用户；同时还
负责内容更新，保证和源站内容的同步。
13/04/2018  Page 168 of 283
根据内容类型和服务种类的不同，分发服务系统分为多个子服务系统，如：网页加速服务、流媒体加速
服务、应用加速服务等。每个子服务系统都是一个分布式的服务集群，由功能类似、地域接近的分布部
署的 Cache 集群组成。
在承担内容同步、更新和响应用户请求之外，分发服务系统还需要向上层的管理调度系统反馈各个
Cache 设备的健康状况、响应情况、内容缓存状况等，以便管理调度系统能够根据设定的策略决定由
哪个 Cache 设备来响应用户的请求。
9.1.5.2.  负载均衡系统： 负载均衡系统：
负载均衡系统是整个 CDN 系统的中枢。负责对所有的用户请求进行调度，确定提供给用户的最终访问
地址。
使用分级实现。最基本的两极调度体系包括全局负载均衡（GSLB）和本地负载均衡（SLB）。
GSLB 根据用户地址和用户请求的内容，主要根据就近性原则，确定向用户服务的节点。一般通过 DNS
解析或者应用层重定向（Http 3XX 重定向）的方式实现。
SLB 主要负责节点内部的负载均衡。当用户请求从 GSLB 调度到 SLB 时，SLB 会根据节点内各个
Cache 设备的工作状况和内容分布情况等对用户请求重定向。SLB 的实现有四层调度（LVS）、七层调
度（Nginx）和链路负载调度等。
9.1.5.3.  管理系统： 管理系统：
分为运营管理和网络管理子系统。
网络管理系统实现对 CDN 系统的设备管理、拓扑管理、链路监控和故障管理，为管理员提供对全网资
源的可视化的集中管理，通常用 web 方式实现。
运营管理是对 CDN 系统的业务管理，负责处理业务层面的与外界系统交互所必须的一些收集、整理、
交付工作。包括用户管理、产品管理、计费管理、统计分析等。
13/04/2018  Page 169 of 283
10.  日志
10.1.1.  Slf4j
slf4j 的全称是 Simple Loging Facade For Java，即它仅仅是一个为 Java 程序提供日志输出的统一接
口，并不是一个具体的日志实现方案，就比如 JDBC 一样，只是一种规则而已。所以单独的 slf4j 是不
能工作的，必须搭配其他具体的日志实现方案，比如 apache 的 org.apache.log4j.Logger，jdk 自带
的 java.util.logging.Logger 等。
10.1.2.  Log4j
Log4j 是 Apache 的一个开源项目，通过使用 Log4j，我们可以控制日志信息输送的目的地是控制台、
文件、GUI 组件，甚至是套接口服务器、NT 的事件记录器、UNIX Syslog 守护进程等；我们也可以控
制每一条日志的输出格式；通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程。
Log4j 由三个重要的组成构成：日志记录器(Loggers)，输出端(Appenders)和日志格式化器(Layout)。
1.Logger：控制要启用或禁用哪些日志记录语句，并对日志信息进行级别限制
2.Appenders : 指定了日志将打印到控制台还是文件中
3.Layout : 控制日志信息的显示格式
Log4j 中将要输出的 Log 信息定义了 5 种级别，依次为 DEBUG、INFO、WARN、ERROR 和 FATAL，
当输出时，只有级别高过配置中规定的 级别的信息才能真正的输出，这样就很方便的来配置不同情况
下要输出的内容，而不需要更改代码。
10.1.3.  LogBack
简单地说，Logback 是一个 Java 领域的日志框架。它被认为是 Log4J 的继承人。
Logback 主要由三个模块组成：logback-core，logback-classic。logback-access
logback-core 是其它模块的基础设施，其它模块基于它构建，显然，logback-core 提供了一些关键的
通用机制。
logback-classic 的地位和作用等同于 Log4J，它也被认为是 Log4J 的一个改进版，并且它实现了简单
日志门面 SLF4J；
logback-access 主要作为一个与 Servlet 容器交互的模块，比如说 tomcat 或者 jetty，提供一些与
HTTP 访问相关的功能。
10.1.3.1. Logback 优点
  同样的代码路径，Logback 执行更快
  更充分的测试
  原生实现了 SLF4J API（Log4J 还需要有一个中间转换层）
  内容更丰富的文档
  支持 XML 或者 Groovy 方式配置
  配置文件自动热加载
13/04/2018  Page 170 of 283
  从 IO 错误中优雅恢复
  自动删除日志归档
  自动压缩日志成为归档文件
  支持 Prudent 模式，使多个 JVM 进程能记录同一个日志文件
  支持配置文件中加入条件判断来适应不同的环境
  更强大的过滤器
  支持 SiftingAppender（可筛选 Appender）
  异常栈信息带有包信息
10.1.4.  ELK
ELK 是软件集合 Elasticsearch、Logstash、Kibana 的简称，由这三个软件及其相关的组件可以打
造大规模日志实时处理系统。
  Elasticsearch 是一个基于 Lucene 的、支持全文索引的分布式存储和索引引擎，主要负责将
日志索引并存储起来，方便业务方检索查询。
  Logstash 是一个日志收集、过滤、转发的中间件，主要负责将各条业务线的各类日志统一收
集、过滤后，转发给 Elasticsearch 进行下一步处理。
  Kibana 是一个可视化工具，主要负责查询 Elasticsearch 的数据并以可视化的方式展现给业
务方，比如各类饼图、直方图、区域图等。
13/04/2018  Page 171 of 283
11. Zookeeper
11.1.1.  Zookeeper  概念
Zookeeper 是一个分布式协调服务，可用于服务发现，分布式锁，分布式领导选举，配置管理等。
Zookeeper 提供了一个类似于 Linux 文件系统的树形结构（可认为是轻量级的内存文件系统，但
只适合存少量信息，完全不适合存储大量文件或者大文件），同时提供了对于每个节点的监控与
通知机制。
11.1.1.  Zookeeper  角色
Zookeeper 集群是一个基于主从复制的高可用集群，每个服务器承担如下三种角色中的一种
11.1.1.1. Leader
1.  一个 Zookeeper 集群同一时间只会有一个实际工作的 Leader，它会发起并维护与各 Follwer
及 Observer 间的心跳。
2.  所有的写操作必须要通过 Leader 完成再由 Leader 将写操作广播给其它服务器。只要有超过
半数节点（不包括 observeer 节点）写入成功，该写请求就会被提交（类 2PC 协议）。
11.1.1.2. Follower
1.  一个 Zookeeper 集群可能同时存在多个 Follower，它会响应 Leader 的心跳，
2.  Follower 可直接处理并返回客户端的读请求，同时会将写请求转发给 Leader 处理，
3.  并且负责在 Leader 处理写请求时对请求进行投票。
11.1.1.3. Observer
角色与 Follower 类似，但是无投票权。Zookeeper 需保证高可用和强一致性，为了支持更多的客
户端，需要增加更多 Server；Server 增多，投票阶段延迟增大，影响性能；引入 Observer，
Observer 不参与投票； Observers 接受客户端的连接，并将写请求转发给 leader 节点； 加入更
多 Observer 节点，提高伸缩性，同时不影响吞吐率。
13/04/2018  Page 172 of 283
11.1.1.1. ZAB 协议
事务编号 Zxid （事务请求 计数器 + epoch ）
在 ZAB ( ZooKeeper Atomic Broadcast , ZooKeeper 原子消息广播协议） 协议的事务编号 Zxid
设计中，Zxid 是一个 64 位的数字，其中低 32 位是一个简单的单调递增的计数器，针对客户端每
一个事务请求，计数器加 1；而高 32 位则代表 Leader 周期 epoch 的编号，每个当选产生一个新
的 Leader 服务器，就会从这个 Leader 服务器上取出其本地日志中最大事务的 ZXID，并从中读取
epoch 值，然后加 1，以此作为新的 epoch，并将低 32 位从 0 开始计数。
Zxid（Transaction id）类似于 RDBMS 中的事务 ID，用于标识一次更新操作的 Proposal（提议）
ID。为了保证顺序性，该 zkid 必须单调递增。
epoch
epoch：可以理解为当前集群所处的年代或者周期，每个 leader 就像皇帝，都有自己的年号，所
以每次改朝换代，leader 变更之后，都会在前一个年代的基础上加 1。这样就算旧的 leader 崩溃
恢复之后，也没有人听他的了，因为 follower 只听从当前年代的 leader 的命令。
Zab 协议有两种模式 - 恢复模式（选主）、广播模式（同步 ）
Zab协议有两种模式，它们分别是恢复模式（选主）和广播模式（同步）。当服务启动或者在领导
者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多数 Server 完成了和 leader 的状
态同步以后，恢复模式就结束了。状态同步保证了 leader 和 Server 具有相同的系统状态。
ZAB 协议 4 阶段
Leader election （选举阶段 - 选出准 Leader ）
1.  Leader election（选举阶段）：节点在一开始都处于选举阶段，只要有一个节点得到超半数
节点的票数，它就可以当选准 leader。只有到达 广播阶段（broadcast） 准 leader 才会成
为真正的 leader。这一阶段的目的是就是为了选出一个准 leader，然后进入下一个阶段。
13/04/2018  Page 173 of 283
Discovery （发现阶段 - 接受提议、生成 epoch 、接受 epoch ）
2.  Discovery（发现阶段）：在这个阶段，followers 跟准 leader 进行通信，同步 followers
最近接收的事务提议。这个一阶段的主要目的是发现当前大多数节点接收的最新提议，并且
准 leader 生成新的 epoch，让 followers 接受，更新它们的 accepted Epoch
一个 follower 只会连接一个 leader，如果有一个节点 f 认为另一个 follower p 是 leader，f
在尝试连接 p 时会被拒绝，f 被拒绝之后，就会进入重新选举阶段。
Synchronization （同步阶段 - 同步 follower 副本 ）
3.  Synchronization（同步阶段）：同步阶段主要是利用 leader 前一阶段获得的最新提议历史，
同步集群中所有的副本。只有当 大多数节点都同步完成，准 leader 才会成为真正的 leader。
follower 只会接收 zxid 比自己的 lastZxid 大的提议。
Broadcast （广播阶段 -leader 消息广播 ）
4.  Broadcast（广播阶段）：到了这个阶段，Zookeeper 集群才能正式对外提供事务服务，
并且 leader 可以进行消息广播。同时如果有新的节点加入，还需要对新节点进行同步。
ZAB 提交事务并不像 2PC 一样需要全部 follower 都 ACK，只需要得到超过半数的节点的 ACK 就
可以了。
ZAB 协议 JAVA 实现 （ FLE-发现阶段和同步合并为 Recovery Phase（恢复阶段） ）
协议的 Java 版本实现跟上面的定义有些不同，选举阶段使用的是 Fast Leader Election（FLE），
它包含了 选举的发现职责。因为 FLE 会选举拥有最新提议历史的节点作为 leader，这样就省去了
发现最新提议的步骤。实际的实现将 发现阶段 和 同步合并为 Recovery Phase（恢复阶段）。所
以，ZAB 的实现只有三个阶段：Fast Leader Election；Recovery Phase；Broadcast Phase。
11.1.1.2. 投票机制
每个 sever 首先给自己投票，然后用自己的选票和其他 sever 选票对比，权重大的胜出，使用权
重较大的更新自身选票箱。具体选举过程如下：
1.  每个 Server 启动以后都询问其它的 Server 它要投票给谁。对于其他 server 的询问，
server 每次根据自己的状态都回复自己推荐的 leader 的 id 和上一次处理事务的 zxid（系
统启动时每个 server 都会推荐自己）
2.  收到所有 Server 回复以后，就计算出 zxid 最大的哪个 Server，并将这个 Server 相关信
息设置成下一次要投票的 Server。
3.  计算这过程中获得票数最多的的 sever 为获胜者，如果获胜者的票数超过半数，则改
server 被选为 leader。否则，继续这个过程，直到 leader 被选举出来
4.  leader 就会开始等待 server 连接
5.  Follower 连接 leader，将最大的 zxid 发送给 leader
6.  Leader 根据 follower 的 zxid 确定同步点，至此选举阶段完成。
7.  选举阶段完成 Leader 同步后通知 follower 已经成为 uptodate 状态
8.  Follower 收到 uptodate 消息后，又可以重新接受 client 的请求进行服务了
13/04/2018  Page 174 of 283
目前有 5 台服务器，每台服务器均没有数据，它们的编号分别是 1,2,3,4,5,按编号依次启动，它们
的选择举过程如下：
1.  服务器 1 启动，给自己投票，然后发投票信息，由于其它机器还没有启动所以它收不到反
馈信息，服务器 1 的状态一直属于 Looking。
2.  服务器 2 启动，给自己投票，同时与之前启动的服务器 1 交换结果，由于服务器 2 的编号
大所以服务器 2 胜出，但此时投票数没有大于半数，所以两个服务器的状态依然是
LOOKING。
3.  服务器 3 启动，给自己投票，同时与之前启动的服务器 1,2 交换信息，由于服务器 3 的编
号最大所以服务器 3 胜出，此时投票数正好大于半数，所以服务器 3 成为领导者，服务器
1,2 成为小弟。
4.  服务器 4 启动，给自己投票，同时与之前启动的服务器 1,2,3 交换信息，尽管服务器 4 的
编号大，但之前服务器 3 已经胜出，所以服务器 4 只能成为小弟。
5.  服务器 5 启动，后面的逻辑同服务器 4 成为小弟。
11.1.2.  Zookeeper 工作原理 工作原理 （ 原子广播） ）
1.  Zookeeper 的核心是原子广播，这个机制保证了各个 server 之间的同步。实现这个机制
的协议叫做 Zab 协议。Zab 协议有两种模式，它们分别是恢复模式和广播模式。
2.  当服务启动或者在领导者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多
数 server 的完成了和 leader 的状态同步以后，恢复模式就结束了。
3.  状态同步保证了 leader 和 server 具有相同的系统状态
4.  一旦 leader 已经和多数的 follower 进行了状态同步后，他就可以开始广播消息了，即进
入广播状态。这时候当一个 server 加入 zookeeper 服务中，它会在恢复模式下启动，发
现 leader，并和 leader 进行状态同步。待到同步结束，它也参与消息广播。Zookeeper
服务一直维持在 Broadcast 状态，直到 leader 崩溃了或者 leader 失去了大部分的
followers 支持。
5.  广播模式需要保证 proposal 被按顺序处理，因此 zk 采用了递增的事务 id 号(zxid)来保
证。所有的提议(proposal)都在被提出的时候加上了 zxid。
6.  实现中 zxid 是一个 64 为的数字，它高 32 位是 epoch 用来标识 leader 关系是否改变，
每次一个 leader 被选出来，它都会有一个新的 epoch。低 32 位是个递增计数。
7.  当 leader 崩溃或者 leader 失去大多数的 follower，这时候 zk 进入恢复模式，恢复模式
需要重新选举出一个新的 leader，让所有的 server 都恢复到一个正确的状态。
11.1.3.  Znode  有四种形式的目录节点
1.  PERSISTENT：持久的节点。
2.  EPHEMERAL：暂时的节点。
3.  PERSISTENT_SEQUENTIAL：持久化顺序编号目录节点。
4.  EPHEMERAL_SEQUENTIAL：暂时化顺序编号目录节点。
13/04/2018  Page 175 of 283
12. Kafka
12.1.1.  Kafka  概念
Kafka 是一种高吞吐量、分布式、基于发布/订阅的消息系统，最初由 LinkedIn 公司开发，使用
Scala 语言编写，目前是 Apache 的开源项目。
1.  broker：Kafka 服务器，负责消息存储和转发
2.  topic：消息类别，Kafka 按照 topic 来分类消息
3.  partition：topic 的分区，一个 topic 可以包含多个 partition，topic 消息保存在各个
partition 上
4.  offset：消息在日志中的位置，可以理解是消息在 partition 上的偏移量，也是代表该消息的
唯一序号
5.  Producer：消息生产者
6.  Consumer：消息消费者
7.  Consumer Group：消费者分组，每个 Consumer 必须属于一个 group
8.  Zookeeper：保存着集群 broker、topic、partition 等 meta 数据；另外，还负责 broker 故
障发现，partition leader 选举，负载均衡等功能
12.1.2.  Kafka 数据 数据存储设计 存储设计
12.1.2.1. partition 的数据文件（ offset，MessageSize，data ）
partition中的每条Message包含了以下三个属性：offset，MessageSize，data，其中offset表
示 Message 在这个 partition 中的偏移量，offset 不是该 Message 在 partition 数据文件中的实
13/04/2018  Page 176 of 283
际存储位置，而是逻辑上一个值，它唯一确定了partition中的一条Message，可以认为offset是
partition 中 Message 的 id；MessageSize 表示消息内容 data 的大小；data 为 Message 的具
体内容。
12.1.2.2. 数据文件分段 segment（ 顺序读写、分段命令、二分查找 ）
partition 物理上由多个 segment 文件组成，每个 segment 大小相等，顺序读写。每个 segment
数据文件以该段中最小的 offset 命名，文件扩展名为.log。这样在查找指定 offset 的 Message 的
时候，用二分查找就可以定位到该 Message 在哪个 segment 数据文件中。
12.1.2.3. 数据文件索引（分段索引、 稀疏存储 ）
Kafka 为每个分段后的数据文件建立了索引文件，文件名与数据文件的名字是一样的，只是文件扩
展名为.index。index 文件中并没有为数据文件中的每条 Message 建立索引，而是采用了稀疏存
储的方式，每隔一定字节的数据建立一条索引。这样避免了索引文件占用过多的空间，从而可以
将索引文件保留在内存中。
12.1.3.  生产者设计 生产者设计
12.1.3.1. 负载均衡（partition 会均衡分布到不同 broker 上）
由于消息 topic 由多个 partition 组成，且 partition 会均衡分布到不同 broker 上，因此，为了有
效利用 broker 集群的性能，提高消息的吞吐量，producer 可以通过随机或者 hash 等方式，将消
息平均发送到多个 partition 上，以实现负载均衡。
13/04/2018  Page 177 of 283
12.1.3.2. 批量发送
是提高消息吞吐量重要的方式，Producer 端可以在内存中合并多条消息后，以一次请求的方式发
送了批量的消息给 broker，从而大大减少 broker 存储消息的 IO 操作次数。但也一定程度上影响
了消息的实时性，相当于以时延代价，换取更好的吞吐量。
12.1.3.3. 压缩（ GZIP 或 Snappy ）
Producer 端可以通过 GZIP 或 Snappy 格式对消息集合进行压缩。Producer 端进行压缩之后，在
Consumer 端需进行解压。压缩的好处就是减少传输的数据量，减轻对网络传输的压力，在对大
数据处理上，瓶颈往往体现在网络上而不是 CPU（压缩和解压会耗掉部分 CPU 资源）。
12.1.1.  消费 者设计 设计
13/04/2018  Page 178 of 283
12.1.1.1. Consumer Group
同一 Consumer Group 中的多个 Consumer 实例，不同时消费同一个 partition，等效于队列模
式。partition 内消息是有序的，Consumer 通过 pull 方式消费消息。Kafka 不删除已消费的消息
对于 partition，顺序读写磁盘数据，以时间复杂度 O(1)方式提供消息持久化能力。
13/04/2018  Page 179 of 283
13. RabbitMQ
13.1.1.  概念 概念
RabbitMQ 是一个由 Erlang 语言开发的 AMQP 的开源实现。
AMQP ：Advanced Message Queue，高级消息队列协议。它是应用层协议的一个开放标准，为
面向消息的中间件设计，基于此协议的客户端与消息中间件可传递消息，并不受产品、开发语言
等条件的限制。
RabbitMQ 最初起源于金融系统，用于在分布式系统中存储转发消息，在易用性、扩展性、高可
用性等方面表现不俗。具体特点包括：
1.  可靠性（Reliability）：RabbitMQ 使用一些机制来保证可靠性，如持久化、传输确认、发布
确认。
2.  灵活的路由（Flexible Routing）：在消息进入队列之前，通过 Exchange 来路由消息的。对
于典型的路由功能，RabbitMQ 已经提供了一些内置的 Exchange 来实现。针对更复杂的路
由功能，可以将多个 Exchange 绑定在一起，也通过插件机制实现自己的 Exchange 。
3.  消息集群（Clustering）：多个 RabbitMQ 服务器可以组成一个集群，形成一个逻辑 Broker 。
4.  高可用（Highly Available Queues）：队列可以在集群中的机器上进行镜像，使得在部分节
点出问题的情况下队列仍然可用。
5.  多种协议（Multi-protocol）：RabbitMQ 支持多种消息队列协议，比如 STOMP、MQTT
等等。
6.  多语言客户端（Many Clients）：RabbitMQ 几乎支持所有常用语言，比如 Java、.NET、
Ruby 等等。
7.  管理界面（Management UI）:RabbitMQ 提供了一个易用的用户界面，使得用户可以监控
和管理消息 Broker 的许多方面。
8.  跟踪机制（Tracing）:如果消息异常，RabbitMQ 提供了消息跟踪机制，使用者可以找出发生
了什么。
9.  插件机制（Plugin System）:RabbitMQ 提供了许多插件，来从多方面进行扩展，也可以编
写自己的插件。
13.1.2.  RabbitMQ  架构
13/04/2018  Page 180 of 283
13.1.2.1. Message
消息，消息是不具名的，它由消息头和消息体组成。消息体是不透明的，而消息头则由一系
列的可选属性组成，这些属性包括 routing-key（路由键）、priority（相对于其他消息的优
先权）、delivery-mode（指出该消息可能需要持久性存储）等。
13.1.2.2. Publisher
1.  消息的生产者，也是一个向交换器发布消息的客户端应用程序。
13.1.2.3. Exchange（将消息路由给队列 ）
2.  交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列。
13.1.2.4. Binding（消息队列和交换器之间的关联）
3.  绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连
接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。
13.1.2.5. Queue
4.  消息队列，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息
可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
13.1.2.6. Connection
5.  网络连接，比如一个 TCP 连接。
13.1.2.7. Channel
6.  信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的 TCP 连接内地虚
拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这
些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所
以引入了信道的概念，以复用一条 TCP 连接。
13.1.2.8. Consumer
7.  消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。
13.1.2.9. Virtual Host
8.  虚拟主机，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密
环境的独立服务器域。
13/04/2018  Page 181 of 283
13.1.2.10. Broker
9.  表示消息队列服务器实体。
13.1.3.  Exchange  类型
Exchange 分发消息时根据类型的不同分发策略有区别，目前共四种类型：direct、fanout、
topic、headers 。headers 匹配 AMQP 消息的 header 而不是路由键，此外 headers 交换器和
direct 交换器完全一致，但性能差很多，目前几乎用不到了，所以直接看另外三种类型：
13.1.3.1. Direct 键（routing key）分布：
1.  Direct：消息中的路由键（routing key）如果和 Binding 中的 binding key 一致，
交换器就将消息发到对应的队列中。它是完全匹配、单播的模式。
13.1.3.2. Fanout（广播分发）
2.  Fanout：每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。很像子
网广播，每台子网内的主机都获得了一份复制的消息。fanout 类型转发消息是最快
的。
13/04/2018  Page 182 of 283
13.1.3.3.  topic 交换器（模式匹配）
3.  topic 交换器：topic 交换器通过模式匹配分配消息的路由键属性，将路由键和某个模
式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成
单词，这些单词之间用点隔开。它同样也会识别两个通配符：符号“#”和符号
“”。#匹配 0 个或多个单词，匹配不多不少一个单词。
13/04/2018  Page 183 of 283
14. Hbase
14.1.1.  概念 概念
base 是分布式、面向列的开源数据库（其实准确的说是面向列族）。HDFS 为 Hbase 提供可靠的
底层数据存储服务，MapReduce 为 Hbase 提供高性能的计算能力，Zookeeper 为 Hbase 提供
稳定服务和 Failover 机制，因此我们说 Hbase 是一个通过大量廉价的机器解决海量数据的高速存
储和读取的分布式数据库解决方案。
14.1.2.  列式存储 列式存储
列方式所带来的重要好处之一就是，由于查询中的选择规则是通过列来定义的，因此整个数据库
是自动索引化的。
这里的列式存储其实说的是列族存储，Hbase 是根据列族来存储数据的。列族下面可以有非常多
的列，列族在创建表的时候就必须指定。为了加深对 Hbase 列族的理解，下面是一个简单的关系
型数据库的表和 Hbase 数据库的表：
13/04/2018  Page 184 of 283
14.1.3.  Hbase  核心概念
14.1.3.1. Column Family 列族
Column Family 又叫列族，Hbase 通过列族划分数据的存储，列族下面可以包含任意多的列，实
现灵活的数据存取。Hbase 表的创建的时候就必须指定列族。就像关系型数据库创建的时候必须
指定具体的列是一样的。Hbase的列族不是越多越好，官方推荐的是列族最好小于或者等于3。我
们使用的场景一般是 1 个列族。
14.1.3.2. Rowkey（ Rowkey 查询，Rowkey 范围扫描，全表扫描 ）
Rowkey 的概念和 mysql 中的主键是完全一样的，Hbase 使用 Rowkey 来唯一的区分某一行的数
据。Hbase 只支持 3 中查询方式：基于 Rowkey 的单行查询，基于 Rowkey 的范围扫描，全表扫
描。
