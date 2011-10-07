begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|pool
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
name|*
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
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
specifier|public
class|class
name|PooledSessionExhaustionTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE
init|=
literal|"FOO"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|700
decl_stmt|;
specifier|private
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|PooledConnectionFactory
name|pooledFactory
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
name|int
name|numReceived
init|=
literal|0
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
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
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|pooledFactory
operator|=
operator|new
name|PooledConnectionFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setBlockIfSessionPoolIsFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_MESSAGES
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|String
name|msgTo
init|=
literal|"hello"
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|msgTo
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"sent "
operator|+
name|i
operator|+
literal|" messages using "
operator|+
name|connectionFactory
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testCanExhaustSessions
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
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
name|NUM_MESSAGES
condition|;
operator|++
name|i
control|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|numReceived
operator|++
expr_stmt|;
if|if
condition|(
name|numReceived
operator|%
literal|20
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"received "
operator|+
name|numReceived
operator|+
literal|" messages "
argument_list|)
expr_stmt|;
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|sendMessages
argument_list|(
name|pooledFactory
argument_list|)
expr_stmt|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MESSAGES
argument_list|,
name|numReceived
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

