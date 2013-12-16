begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|proxy
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|security
operator|.
name|AuthenticationUser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|security
operator|.
name|SimpleAuthenticationPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSSecurityException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ4889Test
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ4889Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GOOD_USER_PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|WRONG_PASSWORD
init|=
literal|"wrongPassword"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_URI
init|=
literal|"tcp://localhost:6002"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_URI
init|=
literal|"tcp://localhost:6001"
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|ProxyConnector
name|proxyConnector
decl_stmt|;
specifier|protected
name|TransportConnector
name|transportConnector
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|ITERATIONS
init|=
literal|100
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<
name|BrokerPlugin
argument_list|>
argument_list|()
decl_stmt|;
name|BrokerPlugin
name|authenticationPlugin
init|=
name|configureAuthentication
argument_list|()
decl_stmt|;
name|plugins
operator|.
name|add
argument_list|(
name|authenticationPlugin
argument_list|)
expr_stmt|;
name|BrokerPlugin
index|[]
name|array
init|=
operator|new
name|BrokerPlugin
index|[
name|plugins
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
name|plugins
operator|.
name|toArray
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
name|transportConnector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|LOCAL_URI
argument_list|)
expr_stmt|;
name|proxyConnector
operator|=
operator|new
name|ProxyConnector
argument_list|()
expr_stmt|;
name|proxyConnector
operator|.
name|setName
argument_list|(
literal|"proxy"
argument_list|)
expr_stmt|;
comment|// TODO rename
name|proxyConnector
operator|.
name|setBind
argument_list|(
operator|new
name|URI
argument_list|(
name|PROXY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|proxyConnector
operator|.
name|setRemote
argument_list|(
operator|new
name|URI
argument_list|(
name|LOCAL_URI
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addProxyConnector
argument_list|(
name|proxyConnector
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
return|return
name|brokerService
return|;
block|}
specifier|protected
name|BrokerPlugin
name|configureAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AuthenticationUser
argument_list|>
name|users
init|=
operator|new
name|ArrayList
argument_list|<
name|AuthenticationUser
argument_list|>
argument_list|()
decl_stmt|;
name|users
operator|.
name|add
argument_list|(
operator|new
name|AuthenticationUser
argument_list|(
name|USER
argument_list|,
name|GOOD_USER_PASSWORD
argument_list|,
literal|"users"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleAuthenticationPlugin
name|authenticationPlugin
init|=
operator|new
name|SimpleAuthenticationPlugin
argument_list|(
name|users
argument_list|)
decl_stmt|;
return|return
name|authenticationPlugin
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|PROXY_URI
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testForConnectionLeak
parameter_list|()
throws|throws
name|Exception
block|{
name|Integer
name|expectedConnectionCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Iteration {} adding bad connection"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|USER
argument_list|,
name|WRONG_PASSWORD
argument_list|)
decl_stmt|;
comment|// TODO change to debug
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"createSession should fail"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Iteration {} adding good connection"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|USER
argument_list|,
name|GOOD_USER_PASSWORD
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|expectedConnectionCount
operator|++
expr_stmt|;
block|}
comment|//
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|e
parameter_list|)
block|{             }
name|LOG
operator|.
name|debug
argument_list|(
literal|"Iteration {} Connections? {}"
argument_list|,
name|i
argument_list|,
name|proxyConnector
operator|.
name|getConnectionCount
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// Need to wait for remove to finish
name|assertEquals
argument_list|(
name|expectedConnectionCount
argument_list|,
name|proxyConnector
operator|.
name|getConnectionCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

