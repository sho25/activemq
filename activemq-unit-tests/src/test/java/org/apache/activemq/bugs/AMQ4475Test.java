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
name|assertFalse
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|DeliveryMode
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
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|IndividualDeadLetterStrategy
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
name|broker
operator|.
name|util
operator|.
name|TimeStampingBrokerPlugin
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
name|ActiveMQQueue
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
name|ActiveMQTextMessage
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

begin_class
specifier|public
class|class
name|AMQ4475Test
block|{
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AMQ4475Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|NUM_MSGS
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|int
name|MAX_THREADS
init|=
literal|20
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|MAX_THREADS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|original
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"jms/AQueue"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|rerouted
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"jms/AQueue_proxy"
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TimeStampingBrokerPlugin
name|tsbp
init|=
operator|new
name|TimeStampingBrokerPlugin
argument_list|()
decl_stmt|;
name|tsbp
operator|.
name|setZeroExpirationOverride
argument_list|(
literal|432000000
argument_list|)
expr_stmt|;
name|tsbp
operator|.
name|setTtlCeiling
argument_list|(
literal|432000000
argument_list|)
expr_stmt|;
name|tsbp
operator|.
name|setFutureOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|tsbp
block|}
argument_list|)
expr_stmt|;
name|connectionUri
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
comment|// Configure Dead Letter Strategy
name|DeadLetterStrategy
name|strategy
init|=
operator|new
name|IndividualDeadLetterStrategy
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setProcessExpired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|IndividualDeadLetterStrategy
operator|)
name|strategy
operator|)
operator|.
name|setUseQueueForQueueMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|IndividualDeadLetterStrategy
operator|)
name|strategy
operator|)
operator|.
name|setQueuePrefix
argument_list|(
literal|"DLQ."
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setProcessNonPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Add policy and individual DLQ strategy
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setTimeBeforeDispatchStarts
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setDeadLetterStrategy
argument_list|(
name|strategy
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
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIndividualDeadLetterAndTimeStampPlugin
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting test .."
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|// Produce to network
name|List
argument_list|<
name|Future
argument_list|<
name|ProducerTask
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|ProducerTask
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
literal|1
condition|;
name|index
operator|++
control|)
block|{
name|ProducerTask
name|p
init|=
operator|new
name|ProducerTask
argument_list|(
name|connectionUri
argument_list|,
name|original
argument_list|,
name|NUM_MSGS
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|ProducerTask
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
name|p
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
name|ForwardingConsumerThread
name|f1
init|=
operator|new
name|ForwardingConsumerThread
argument_list|(
name|original
argument_list|,
name|rerouted
argument_list|,
name|NUM_MSGS
argument_list|)
decl_stmt|;
name|f1
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConsumerThread
name|c1
init|=
operator|new
name|ConsumerThread
argument_list|(
name|connectionUri
argument_list|,
name|rerouted
argument_list|,
name|NUM_MSGS
argument_list|)
decl_stmt|;
name|c1
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting on consumers and producers to exit"
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|Future
argument_list|<
name|ProducerTask
argument_list|>
name|future
range|:
name|tasks
control|)
block|{
name|ProducerTask
name|e
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[Completed] "
operator|+
name|e
operator|.
name|dest
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Producing threads complete, waiting on ACKs"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|join
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|c1
operator|.
name|join
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught unexpected exception: {}"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|assertFalse
argument_list|(
name|f1
operator|.
name|isFailed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c1
operator|.
name|isFailed
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|estimatedTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase duration (seconds): "
operator|+
name|estimatedTime
operator|/
literal|1000000000.0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumers and producers exited, all msgs received as expected"
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|ProducerTask
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|dest
decl_stmt|;
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
specifier|public
name|ProducerTask
parameter_list|(
name|String
name|uri
parameter_list|,
name|ActiveMQQueue
name|dest
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|destName
init|=
literal|""
decl_stmt|;
try|try
block|{
name|destName
operator|=
name|dest
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
literal|"Test Message"
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|msg
operator|+
name|dest
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"["
operator|+
name|destName
operator|+
literal|"] Sent "
operator|+
name|count
operator|+
literal|" msgs"
argument_list|)
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
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
class|class
name|ForwardingConsumerThread
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|ActiveMQQueue
name|original
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|forward
decl_stmt|;
specifier|private
name|int
name|blockSize
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|int
name|PARALLEL
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|failed
decl_stmt|;
specifier|public
name|ForwardingConsumerThread
parameter_list|(
name|ActiveMQQueue
name|original
parameter_list|,
name|ActiveMQQueue
name|forward
parameter_list|,
name|int
name|total
parameter_list|)
block|{
name|this
operator|.
name|original
operator|=
name|original
expr_stmt|;
name|this
operator|.
name|forward
operator|=
name|forward
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|total
operator|/
name|PARALLEL
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|failed
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|PARALLEL
condition|;
name|index
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|forward
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|blockSize
condition|)
block|{
name|Message
name|msg1
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg1
operator|instanceof
name|ActiveMQTextMessage
condition|)
block|{
if|if
condition|(
name|count
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming -> "
operator|+
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|msg1
operator|)
operator|.
name|getDestination
argument_list|()
operator|+
literal|" count="
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping unknown msg type "
operator|+
name|msg1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"["
operator|+
name|original
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"] completed segment ("
operator|+
name|index
operator|+
literal|" of "
operator|+
name|blockSize
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": is stopping"
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|public
class|class
name|ConsumerThread
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|dest
decl_stmt|;
specifier|private
name|int
name|blockSize
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|int
name|PARALLEL
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|failed
decl_stmt|;
specifier|public
name|ConsumerThread
parameter_list|(
name|String
name|uri
parameter_list|,
name|ActiveMQQueue
name|dest
parameter_list|,
name|int
name|total
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|total
operator|/
name|PARALLEL
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|failed
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|PARALLEL
condition|;
name|index
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|blockSize
condition|)
block|{
name|Object
name|msg1
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg1
operator|instanceof
name|ActiveMQTextMessage
condition|)
block|{
if|if
condition|(
name|count
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming -> "
operator|+
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|msg1
operator|)
operator|.
name|getDestination
argument_list|()
operator|+
literal|" count="
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping unknown msg type "
operator|+
name|msg1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|failed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"["
operator|+
name|dest
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"] completed segment ("
operator|+
name|index
operator|+
literal|" of "
operator|+
name|blockSize
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
literal|"Caught unexpected exception: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": is stopping"
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
block|}
end_class

end_unit

