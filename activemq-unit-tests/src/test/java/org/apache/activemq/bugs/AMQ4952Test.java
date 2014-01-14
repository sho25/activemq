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
name|network
operator|.
name|ConditionalNetworkBridgeFilterFactory
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
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|util
operator|.
name|IntrospectionSupport
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|*
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
name|sql
operator|.
name|DataSource
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
name|sql
operator|.
name|*
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
name|Arrays
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
name|concurrent
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test creates a broker network with two brokers -  * producerBroker (with a message producer attached) and consumerBroker (with consumer attached)  *<p/>  * Simulates network duplicate message by stopping and restarting the consumerBroker after message (with message ID ending in  * 120) is persisted to consumerBrokerstore BUT BEFORE ack sent to the producerBroker over the network connection.  * When the network connection is reestablished the producerBroker resends  * message (with messageID ending in 120).  *<p/>  * Expectation:  *<p/>  * With the following policy entries set,  would  expect the duplicate message to be read from the store  * and dispatched to the consumer - where the duplicate could be detected by consumer.  *<p/>  * PolicyEntry policy = new PolicyEntry();  * policy.setQueue(">");  * policy.setEnableAudit(false);  * policy.setUseCache(false);  * policy.setExpireMessagesPeriod(0);  *<p/>  *<p/>  * Note 1: Network needs to use replaywhenNoConsumers so enabling the networkAudit to avoid this scenario is not feasible.  *<p/>  * NOTE 2: Added a custom plugin to the consumerBroker so that the consumerBroker shutdown will occur after a message has been  * persisted to consumerBroker store but before an ACK is sent back to ProducerBroker. This is just a hack to ensure producerBroker will resend  * the message after shutdown.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AMQ4952Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ4952Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|1
decl_stmt|;
specifier|protected
name|BrokerService
name|consumerBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|producerBroker
decl_stmt|;
specifier|protected
name|ActiveMQQueue
name|QUEUE_NAME
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"duptest.store"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|stopConsumerBroker
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|consumerBrokerRestarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|consumerRestartedAndMessageForwarded
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|EmbeddedDataSource
name|localDataSource
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|0
argument_list|)
specifier|public
name|boolean
name|enableCursorAudit
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"enableAudit={0}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|Boolean
operator|.
name|TRUE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConsumerBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|Callable
name|consumeMessageTask
init|=
operator|new
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|receivedMessageCount
init|=
literal|0
decl_stmt|;
name|ActiveMQConnectionFactory
name|consumerFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:(tcp://localhost:2006)?randomize=false&backup=false"
argument_list|)
decl_stmt|;
name|Connection
name|consumerConnection
init|=
name|consumerFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|consumerConnection
operator|.
name|setClientID
argument_list|(
literal|"consumer"
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|textMsg
operator|==
literal|null
condition|)
block|{
return|return
name|receivedMessageCount
return|;
block|}
name|receivedMessageCount
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** receivedMessageCount {} message has MessageID {} "
argument_list|,
name|receivedMessageCount
argument_list|,
name|textMsg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
comment|// on first delivery ensure the message is pending an ack when it is resent from the producer broker
if|if
condition|(
name|textMsg
operator|.
name|getJMSMessageID
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"1"
argument_list|)
operator|&&
name|receivedMessageCount
operator|==
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for restart..."
argument_list|)
expr_stmt|;
name|consumerRestartedAndMessageForwarded
operator|.
name|await
argument_list|(
literal|90
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
name|textMsg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Runnable
name|consumerBrokerResetTask
init|=
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
comment|// wait for signal
name|stopConsumerBroker
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"********* STOPPING CONSUMER BROKER"
argument_list|)
expr_stmt|;
name|consumerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|consumerBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"***** STARTING CONSUMER BROKER"
argument_list|)
expr_stmt|;
comment|// do not delete  messages on startup
name|consumerBroker
operator|=
name|createConsumerBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"***** CONSUMER BROKER STARTED!!"
argument_list|)
expr_stmt|;
name|consumerBrokerRestarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"message forwarded on time"
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ProducerBroker totalMessageCount: "
operator|+
name|producerBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|producerBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|consumerRestartedAndMessageForwarded
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception when stopping/starting the consumerBroker "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|//start consumerBroker start/stop task
name|executor
operator|.
name|execute
argument_list|(
name|consumerBrokerResetTask
argument_list|)
expr_stmt|;
comment|//start consuming messages
name|Future
argument_list|<
name|Integer
argument_list|>
name|numberOfConsumedMessage
init|=
name|executor
operator|.
name|submit
argument_list|(
name|consumeMessageTask
argument_list|)
decl_stmt|;
name|produceMessages
argument_list|()
expr_stmt|;
comment|//Wait for consumer to finish
name|int
name|totalMessagesConsumed
init|=
name|numberOfConsumedMessage
operator|.
name|get
argument_list|()
decl_stmt|;
name|StringBuffer
name|contents
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|boolean
name|messageInStore
init|=
name|isMessageInJDBCStore
argument_list|(
name|localDataSource
argument_list|,
name|contents
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"****number of messages received "
operator|+
name|totalMessagesConsumed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of messages received"
argument_list|,
literal|2
argument_list|,
name|totalMessagesConsumed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"messages left in store"
argument_list|,
literal|true
argument_list|,
name|messageInStore
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"message is in dlq: "
operator|+
name|contents
operator|.
name|toString
argument_list|()
argument_list|,
name|contents
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"DLQ"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|produceMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|producerFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:(tcp://localhost:2003)?randomize=false&backup=false"
argument_list|)
decl_stmt|;
name|Connection
name|producerConnection
init|=
name|producerFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|producerConnection
operator|.
name|setClientID
argument_list|(
literal|"producer"
argument_list|)
expr_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
name|producerConnection
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
specifier|final
name|MessageProducer
name|remoteProducer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|MESSAGE_COUNT
operator|>
name|i
condition|)
block|{
name|String
name|payload
init|=
literal|"test msg "
operator|+
name|i
decl_stmt|;
name|TextMessage
name|msg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|remoteProducer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Before
specifier|public
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
name|doSetUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|doTearDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doTearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|producerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{         }
try|try
block|{
name|consumerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|doSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|producerBroker
operator|=
name|createProducerBroker
argument_list|()
expr_stmt|;
name|consumerBroker
operator|=
name|createConsumerBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Producer broker      * listens on  localhost:2003      * networks to consumerBroker - localhost:2006      *      * @return      * @throws Exception      */
specifier|protected
name|BrokerService
name|createProducerBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|networkToPorts
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"2006"
block|}
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|networkProps
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
name|networkProps
operator|.
name|put
argument_list|(
literal|"networkTTL"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|networkProps
operator|.
name|put
argument_list|(
literal|"conduitSubscriptions"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|networkProps
operator|.
name|put
argument_list|(
literal|"decreaseNetworkConsumerPriority"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|networkProps
operator|.
name|put
argument_list|(
literal|"dynamicOnly"
argument_list|,
literal|"true"
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
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
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
name|setBrokerName
argument_list|(
literal|"BP"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// lazy init listener on broker start
name|TransportConnector
name|transportConnector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|transportConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:2003"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TransportConnector
argument_list|>
name|transportConnectors
init|=
operator|new
name|ArrayList
argument_list|<
name|TransportConnector
argument_list|>
argument_list|()
decl_stmt|;
name|transportConnectors
operator|.
name|add
argument_list|(
name|transportConnector
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setTransportConnectors
argument_list|(
name|transportConnectors
argument_list|)
expr_stmt|;
comment|//network to consumerBroker
if|if
condition|(
name|networkToPorts
operator|!=
literal|null
operator|&&
name|networkToPorts
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"static:(failover:(tcp://localhost:2006)?maxReconnectAttempts=0)?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|NetworkConnector
name|nc
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|networkProps
operator|!=
literal|null
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|nc
argument_list|,
name|networkProps
argument_list|)
expr_stmt|;
block|}
name|nc
operator|.
name|setStaticallyIncludedDestinations
argument_list|(
name|Arrays
operator|.
expr|<
name|ActiveMQDestination
operator|>
name|asList
argument_list|(
operator|new
name|ActiveMQQueue
index|[]
block|{
name|QUEUE_NAME
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Persistence adapter
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|EmbeddedDataSource
name|remoteDataSource
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|remoteDataSource
operator|.
name|setDatabaseName
argument_list|(
literal|"derbyDBRemoteBroker"
argument_list|)
expr_stmt|;
name|remoteDataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|remoteDataSource
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
comment|//set Policy entries
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setEnableAudit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// set replay with no consumers
name|ConditionalNetworkBridgeFilterFactory
name|conditionalNetworkBridgeFilterFactory
init|=
operator|new
name|ConditionalNetworkBridgeFilterFactory
argument_list|()
decl_stmt|;
name|conditionalNetworkBridgeFilterFactory
operator|.
name|setReplayWhenNoConsumers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setNetworkBridgeFilterFactory
argument_list|(
name|conditionalNetworkBridgeFilterFactory
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
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
comment|/**      * consumerBroker      * - listens on localhost:2006      *      * @param deleteMessages - drop messages when broker instance is created      * @return      * @throws Exception      */
specifier|protected
name|BrokerService
name|createConsumerBroker
parameter_list|(
name|boolean
name|deleteMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|scheme
init|=
literal|"tcp"
decl_stmt|;
name|String
name|listenPort
init|=
literal|"2006"
decl_stmt|;
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteMessages
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"BC"
argument_list|)
expr_stmt|;
comment|// lazy init listener on broker start
name|TransportConnector
name|transportConnector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|transportConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
name|scheme
operator|+
literal|"://localhost:"
operator|+
name|listenPort
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TransportConnector
argument_list|>
name|transportConnectors
init|=
operator|new
name|ArrayList
argument_list|<
name|TransportConnector
argument_list|>
argument_list|()
decl_stmt|;
name|transportConnectors
operator|.
name|add
argument_list|(
name|transportConnector
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setTransportConnectors
argument_list|(
name|transportConnectors
argument_list|)
expr_stmt|;
comment|//policy entries
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setEnableAudit
argument_list|(
name|enableCursorAudit
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// set replay with no consumers
name|ConditionalNetworkBridgeFilterFactory
name|conditionalNetworkBridgeFilterFactory
init|=
operator|new
name|ConditionalNetworkBridgeFilterFactory
argument_list|()
decl_stmt|;
name|conditionalNetworkBridgeFilterFactory
operator|.
name|setReplayWhenNoConsumers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setNetworkBridgeFilterFactory
argument_list|(
name|conditionalNetworkBridgeFilterFactory
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
comment|// Persistence adapter
name|JDBCPersistenceAdapter
name|localJDBCPersistentAdapter
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|EmbeddedDataSource
name|localDataSource
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|localDataSource
operator|.
name|setDatabaseName
argument_list|(
literal|"derbyDBLocalBroker"
argument_list|)
expr_stmt|;
name|localDataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
name|localJDBCPersistentAdapter
operator|.
name|setDataSource
argument_list|(
name|localDataSource
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|localJDBCPersistentAdapter
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteMessages
condition|)
block|{
comment|// no plugin on restart
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|MyTestPlugin
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|localDataSource
operator|=
name|localDataSource
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
comment|/**      * Query JDBC Store to see if messages are left      *      * @param dataSource      * @return      * @throws SQLException      */
specifier|private
name|boolean
name|isMessageInJDBCStore
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|StringBuffer
name|stringBuffer
parameter_list|)
throws|throws
name|SQLException
block|{
name|boolean
name|tableHasData
init|=
literal|false
decl_stmt|;
name|String
name|query
init|=
literal|"select * from ACTIVEMQ_MSGS"
decl_stmt|;
name|java
operator|.
name|sql
operator|.
name|Connection
name|conn
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|PreparedStatement
name|s
init|=
name|conn
operator|.
name|prepareStatement
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|ResultSet
name|set
init|=
literal|null
decl_stmt|;
try|try
block|{
name|StringBuffer
name|headers
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|set
operator|=
name|s
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|ResultSetMetaData
name|metaData
init|=
name|set
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|metaData
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
name|headers
operator|.
name|append
argument_list|(
literal|"||"
argument_list|)
expr_stmt|;
block|}
name|headers
operator|.
name|append
argument_list|(
name|metaData
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
operator|+
literal|"||"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|headers
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|set
operator|.
name|next
argument_list|()
condition|)
block|{
name|tableHasData
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|metaData
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
name|stringBuffer
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
block|}
name|stringBuffer
operator|.
name|append
argument_list|(
name|set
operator|.
name|getString
argument_list|(
name|i
argument_list|)
operator|+
literal|"|"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|stringBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|set
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
try|try
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|tableHasData
return|;
block|}
comment|/**      * plugin used to ensure consumerbroker is restared before the network message from producerBroker is acked      */
class|class
name|MyTestPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|MyTestBroker
argument_list|(
name|broker
argument_list|)
return|;
block|}
block|}
class|class
name|MyTestBroker
extends|extends
name|BrokerFilter
block|{
specifier|public
name|MyTestBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Stopping broker on send:  "
operator|+
name|messageSend
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|stopConsumerBroker
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

