begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|jms
operator|.
name|TextMessage
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
name|activemq
operator|.
name|bugs
operator|.
name|AMQ1866
operator|.
name|ConsumerThread
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

begin_class
specifier|public
class|class
name|OutOfOrderTestCase
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|OutOfOrderTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PREFETCH
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_URL
init|=
name|BROKER_URL
operator|+
literal|"?jms.prefetchPolicy.all="
operator|+
name|PREFETCH
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"QUEUE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION
init|=
literal|"QUEUE?consumer.exclusive=true"
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|int
name|seq
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|setUp
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
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|BROKER_URL
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|CONNECTION_URL
argument_list|)
decl_stmt|;
name|connection
operator|=
name|connectionFactory
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Producing messages 0-29 . . ."
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|DESTINATION
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|messageProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
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
literal|30
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"FOO"
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|toString
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|messageProducer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Consuming messages 0-9 . . ."
argument_list|)
expr_stmt|;
name|consumeBatch
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Consuming messages 10-19 . . ."
argument_list|)
expr_stmt|;
name|consumeBatch
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Consuming messages 20-29 . . ."
argument_list|)
expr_stmt|;
name|consumeBatch
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|consumeBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|DESTINATION
argument_list|)
decl_stmt|;
specifier|final
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Message
name|message
init|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|1000L
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"received "
operator|+
name|toString
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message out of order"
argument_list|,
name|createMessageText
argument_list|(
name|seq
operator|++
argument_list|)
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|toString
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|ret
init|=
literal|"received message '"
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|+
literal|"' - "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
condition|)
name|ret
operator|+=
literal|" (redelivered)"
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|private
specifier|static
name|String
name|createMessageText
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
literal|"message #"
operator|+
name|index
return|;
block|}
block|}
end_class

end_unit

