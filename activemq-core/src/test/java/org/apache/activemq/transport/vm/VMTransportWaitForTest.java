begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|vm
package|;
end_package

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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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

begin_class
specifier|public
class|class
name|VMTransportWaitForTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VM_BROKER_URI_NO_WAIT
init|=
literal|"vm://localhost?broker.persistent=false&create=false"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VM_BROKER_URI_WAIT_FOR_START
init|=
name|VM_BROKER_URI_NO_WAIT
operator|+
literal|"&waitForStart=20000"
decl_stmt|;
name|CountDownLatch
name|started
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|gotConnection
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testWaitFor
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|new
name|URI
argument_list|(
name|VM_BROKER_URI_NO_WAIT
argument_list|)
argument_list|)
decl_stmt|;
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect broker not exist exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expectedOnNoBrokerAndNoCreate
parameter_list|)
block|{         }
comment|// spawn a thread that will wait for an embedded broker to start via vm://..
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|started
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|new
name|URI
argument_list|(
name|VM_BROKER_URI_WAIT_FOR_START
argument_list|)
argument_list|)
decl_stmt|;
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|gotConnection
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"unexpected exception:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|started
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"has not got connection"
argument_list|,
name|gotConnection
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"has got connection"
argument_list|,
name|gotConnection
operator|.
name|await
argument_list|(
literal|200
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

