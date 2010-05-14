begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|nio
operator|.
name|SelectChannelConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|HttpTestSupport
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HttpTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|Server
name|server
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|createBroker
init|=
literal|true
decl_stmt|;
specifier|final
name|File
name|homeDir
init|=
operator|new
name|File
argument_list|(
literal|"src/main/webapp/uploads/"
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
operator|new
name|Server
argument_list|()
expr_stmt|;
name|SelectChannelConnector
name|connector
init|=
operator|new
name|SelectChannelConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
literal|8080
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|WebAppContext
name|context
init|=
operator|new
name|WebAppContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setResourceBase
argument_list|(
literal|"src/main/webapp"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForJettySocketToAccept
argument_list|(
literal|"http://localhost:8080"
argument_list|)
expr_stmt|;
if|if
condition|(
name|createBroker
condition|)
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteFile
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
name|homeDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|createBroker
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOHelper
operator|.
name|deleteFile
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|waitForJettySocketToAccept
parameter_list|(
name|String
name|bindLocation
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|bindLocation
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Jetty endpoint is available"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|canConnect
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Socket
name|socket
init|=
name|SocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createSocket
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|,
name|url
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|canConnect
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"verify jetty available, failed to connect to "
operator|+
name|url
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|canConnect
return|;
block|}
block|}
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

