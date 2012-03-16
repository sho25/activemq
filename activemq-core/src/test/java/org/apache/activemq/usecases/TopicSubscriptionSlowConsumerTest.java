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
name|advisory
operator|.
name|AdvisorySupport
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Checks to see if "slow consumer advisory messages" are generated when   * small number of messages (2) are published to a topic which has a subscriber   * with a prefetch of one set.  *   */
end_comment

begin_class
specifier|public
class|class
name|TopicSubscriptionSlowConsumerTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_NAME
init|=
literal|"slow.consumer"
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|destination
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
name|BrokerService
name|brokerService
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
name|createBroker
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|activeMQConnectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|activeMQConnectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|=
name|activeMQConnectionFactory
operator|.
name|createConnection
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
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPrefetchValueOne
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|consumerDestination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
operator|+
literal|"?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
expr_stmt|;
comment|//add a consumer to the slow consumer advisory topic.
name|ActiveMQTopic
name|slowConsumerAdvisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getSlowConsumerAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MessageConsumer
name|slowConsumerAdvisory
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|slowConsumerAdvisoryTopic
argument_list|)
decl_stmt|;
comment|//publish 2 messages
name|Message
name|txtMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Sample Text Message"
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|txtMessage
argument_list|)
expr_stmt|;
block|}
comment|//consume 2 messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|receivedMsg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"received msg "
operator|+
name|i
operator|+
literal|" should not be null"
argument_list|,
name|receivedMsg
argument_list|)
expr_stmt|;
block|}
comment|//check for "slow consumer" advisory message
name|Message
name|slowAdvisoryMessage
init|=
name|slowConsumerAdvisory
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"should not have received a slow consumer advisory message"
argument_list|,
name|slowAdvisoryMessage
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//helper method to create a broker with slow consumer advisory turned on
specifier|private
name|BrokerService
name|createBroker
parameter_list|()
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
literal|"localhost"
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
name|setDeleteAllMessagesOnStartup
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
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setAdvisoryForSlowConsumers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
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
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

