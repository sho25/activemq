begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|usecases
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Connection
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
name|MessageListener
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|network
operator|.
name|NetworkConnector
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|NoDuplicateOnTopicNetworkTest
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
name|NoDuplicateOnTopicNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MULTICAST_DEFAULT
init|=
literal|"multicast://default"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_1
init|=
literal|"tcp://localhost:61626"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_2
init|=
literal|"tcp://localhost:61636"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_3
init|=
literal|"tcp://localhost:61646"
decl_stmt|;
specifier|private
name|BrokerService
name|broker1
decl_stmt|;
specifier|private
name|BrokerService
name|broker2
decl_stmt|;
specifier|private
name|BrokerService
name|broker3
decl_stmt|;
specifier|private
name|boolean
name|dynamicOnly
init|=
literal|false
decl_stmt|;
comment|// no duplicates in cyclic network if networkTTL<=1
comment|// when> 1, subscriptions perculate around resulting in duplicates as there is no
comment|// memory of the original subscription.
comment|// solution for 6.0 using org.apache.activemq.command.ConsumerInfo.getNetworkConsumerIds()
specifier|private
name|int
name|ttl
init|=
literal|3
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker3
operator|=
name|createAndStartBroker
argument_list|(
literal|"broker3"
argument_list|,
name|BROKER_3
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|broker2
operator|=
name|createAndStartBroker
argument_list|(
literal|"broker2"
argument_list|,
name|BROKER_2
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|broker1
operator|=
name|createAndStartBroker
argument_list|(
literal|"broker1"
argument_list|,
name|BROKER_1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createAndStartBroker
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|addr
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|addr
argument_list|)
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
name|MULTICAST_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
name|MULTICAST_DEFAULT
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setDynamicOnly
argument_list|(
name|dynamicOnly
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setNetworkTTL
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker3
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testProducerConsumerTopic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|topicName
init|=
literal|"broadcast"
decl_stmt|;
name|Thread
name|producerThread
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
name|TopicWithDuplicateMessages
name|producer
init|=
operator|new
name|TopicWithDuplicateMessages
argument_list|()
decl_stmt|;
name|producer
operator|.
name|setBrokerURL
argument_list|(
name|BROKER_1
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTopicName
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
try|try
block|{
name|producer
operator|.
name|produce
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|TopicWithDuplicateMessages
name|consumer
init|=
operator|new
name|TopicWithDuplicateMessages
argument_list|()
decl_stmt|;
name|Thread
name|consumerThread
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
name|consumer
operator|.
name|setBrokerURL
argument_list|(
name|BROKER_2
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setTopicName
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|consumer
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|getLatch
argument_list|()
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|consumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started Consumer"
argument_list|)
expr_stmt|;
comment|// ensure subscription has percolated though the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started Producer"
argument_list|)
expr_stmt|;
name|producerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|msg
range|:
name|consumer
operator|.
name|getMessageStrings
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"is not a duplicate: "
operator|+
name|msg
argument_list|,
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|msg
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"got all required messages: "
operator|+
name|map
operator|.
name|size
argument_list|()
argument_list|,
name|consumer
operator|.
name|getNumMessages
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
class|class
name|TopicWithDuplicateMessages
block|{
specifier|private
name|String
name|brokerURL
decl_stmt|;
specifier|private
name|String
name|topicName
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Topic
name|topic
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|receivedStrings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|numMessages
init|=
literal|10
decl_stmt|;
specifier|private
name|CountDownLatch
name|recievedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numMessages
argument_list|)
decl_stmt|;
specifier|public
name|CountDownLatch
name|getLatch
parameter_list|()
block|{
return|return
name|recievedLatch
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getMessageStrings
parameter_list|()
block|{
return|return
name|receivedStrings
return|;
block|}
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|brokerURL
return|;
block|}
specifier|public
name|void
name|setBrokerURL
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|brokerURL
expr_stmt|;
block|}
specifier|public
name|String
name|getTopicName
parameter_list|()
block|{
return|return
name|topicName
return|;
block|}
specifier|public
name|void
name|setTopicName
parameter_list|(
name|String
name|topicName
parameter_list|)
block|{
name|this
operator|.
name|topicName
operator|=
name|topicName
expr_stmt|;
block|}
specifier|private
name|void
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createTopic
parameter_list|()
throws|throws
name|JMSException
block|{
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
name|topic
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createProducer
parameter_list|()
throws|throws
name|JMSException
block|{
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|arg0
parameter_list|)
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|arg0
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received message ["
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|receivedStrings
operator|.
name|add
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|recievedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected :"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|publish
parameter_list|()
throws|throws
name|JMSException
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
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"message: "
operator|+
name|i
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending message["
operator|+
name|message
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|produce
parameter_list|()
throws|throws
name|JMSException
block|{
name|createConnection
argument_list|()
expr_stmt|;
name|createTopic
argument_list|()
expr_stmt|;
name|createProducer
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|consumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|createConnection
argument_list|()
expr_stmt|;
name|createTopic
argument_list|()
expr_stmt|;
name|createConsumer
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getNumMessages
parameter_list|()
block|{
return|return
name|numMessages
return|;
block|}
block|}
block|}
end_class

end_unit

