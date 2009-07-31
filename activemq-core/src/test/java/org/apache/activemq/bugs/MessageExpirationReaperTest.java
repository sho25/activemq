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
name|assertFalse
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
name|ActiveMQConnection
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
name|broker
operator|.
name|jmx
operator|.
name|DestinationViewMBean
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
name|ActiveMQDestination
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
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueBrowser
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
name|management
operator|.
name|ObjectName
import|;
end_import

begin_comment
comment|/**  * Test to determine if expired messages are being reaped if there is  * no active consumer connected to the broker.   *   * @author bsnyder  *  */
end_comment

begin_class
specifier|public
class|class
name|MessageExpirationReaperTest
block|{
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|protected
name|String
name|destinationName
init|=
literal|"TEST.Q"
decl_stmt|;
specifier|protected
name|String
name|brokerUrl
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|protected
name|String
name|brokerName
init|=
literal|"testBroker"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|()
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
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
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createBroker
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|brokerUrl
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
name|setExpireMessagesPeriod
argument_list|(
literal|500
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
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
return|;
block|}
specifier|protected
name|Session
name|createSession
parameter_list|()
throws|throws
name|Exception
block|{
return|return
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
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpiredMessageReaping
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|producerSession
init|=
name|createSession
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|producerSession
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
literal|3
decl_stmt|;
comment|// Send some messages with an expiration
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// Let the messages expire
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|DestinationViewMBean
name|view
init|=
name|createView
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect inflight count: "
operator|+
name|view
operator|.
name|getInFlightCount
argument_list|()
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getInFlightCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect queue size count"
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect expired size count"
argument_list|,
name|view
operator|.
name|getEnqueueCount
argument_list|()
argument_list|,
name|view
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Send more messages with an expiration
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// Let the messages expire
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Simply browse the queue
name|Session
name|browserSession
init|=
name|createSession
argument_list|()
decl_stmt|;
name|QueueBrowser
name|browser
init|=
name|browserSession
operator|.
name|createBrowser
argument_list|(
operator|(
name|Queue
operator|)
name|destination
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"no message in the browser"
argument_list|,
name|browser
operator|.
name|getEnumeration
argument_list|()
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
comment|// The messages expire and should be reaped because of the presence of
comment|// the queue browser
name|assertEquals
argument_list|(
literal|"Wrong inFlightCount: "
operator|+
name|view
operator|.
name|getInFlightCount
argument_list|()
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getInFlightCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DestinationViewMBean
name|createView
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
name|ObjectName
name|name
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
name|domain
operator|+
literal|":BrokerName="
operator|+
name|brokerName
operator|+
literal|",Type=Queue,Destination="
operator|+
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
name|domain
operator|+
literal|":BrokerName="
operator|+
name|brokerName
operator|+
literal|",Type=Topic,Destination="
operator|+
name|destinationName
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|DestinationViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|DestinationViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

