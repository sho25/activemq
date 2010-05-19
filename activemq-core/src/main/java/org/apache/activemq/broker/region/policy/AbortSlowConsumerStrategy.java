begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
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
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicBoolean
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
name|Connection
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
name|ConnectionContext
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
name|Subscription
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
name|ConsumerControl
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
name|thread
operator|.
name|Scheduler
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
name|transport
operator|.
name|InactivityIOException
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

begin_comment
comment|/**  * Abort slow consumers when they reach the configured threshold of slowness, default is slow for 30 seconds  *   * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|AbortSlowConsumerStrategy
implements|implements
name|SlowConsumerStrategy
implements|,
name|Runnable
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
name|AbortSlowConsumerStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Scheduler
name|scheduler
init|=
name|Scheduler
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
name|AtomicBoolean
name|taskStarted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|slowConsumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|maxSlowCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|maxSlowDuration
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|long
name|checkPeriod
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|abortConnection
init|=
literal|false
decl_stmt|;
specifier|public
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{
if|if
condition|(
name|maxSlowCount
operator|<
literal|0
operator|&&
name|maxSlowDuration
operator|<
literal|0
condition|)
block|{
comment|// nothing to do
name|LOG
operator|.
name|info
argument_list|(
literal|"no limits set, slowConsumer strategy has nothing to do"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|taskStarted
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|executePeriodically
argument_list|(
name|this
argument_list|,
name|checkPeriod
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|slowConsumers
operator|.
name|containsKey
argument_list|(
name|subs
argument_list|)
condition|)
block|{
name|slowConsumers
operator|.
name|put
argument_list|(
name|subs
argument_list|,
operator|new
name|SlowConsumerEntry
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maxSlowCount
operator|>
literal|0
condition|)
block|{
name|slowConsumers
operator|.
name|get
argument_list|(
name|subs
argument_list|)
operator|.
name|slow
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|maxSlowDuration
operator|>
literal|0
condition|)
block|{
comment|// mark
for|for
control|(
name|SlowConsumerEntry
name|entry
range|:
name|slowConsumers
operator|.
name|values
argument_list|()
control|)
block|{
name|entry
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|HashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|toAbort
init|=
operator|new
name|HashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|entry
range|:
name|slowConsumers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|isSlowConsumer
argument_list|()
condition|)
block|{
if|if
condition|(
name|maxSlowDuration
operator|>
literal|0
operator|&&
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|markCount
operator|*
name|checkPeriod
operator|>
name|maxSlowDuration
operator|)
operator|||
name|maxSlowCount
operator|>
literal|0
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|slowCount
operator|>
name|maxSlowCount
condition|)
block|{
name|toAbort
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|slowConsumers
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sub: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" is no longer slow"
argument_list|)
expr_stmt|;
name|slowConsumers
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|Entry
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|entry
range|:
name|toAbort
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ConnectionContext
name|connectionContext
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|context
decl_stmt|;
if|if
condition|(
name|connectionContext
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"aborting "
operator|+
operator|(
name|abortConnection
condition|?
literal|"connection"
else|:
literal|"consumer"
operator|)
operator|+
literal|", slow consumer: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|connectionContext
operator|.
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|abortConnection
condition|)
block|{
name|scheduler
operator|.
name|executeAfterDelay
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
name|connection
operator|.
name|serviceException
argument_list|(
operator|new
name|InactivityIOException
argument_list|(
literal|"Consumer was slow too often (>"
operator|+
name|maxSlowCount
operator|+
literal|") or too long (>"
operator|+
name|maxSlowDuration
operator|+
literal|"): "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// just abort the consumer by telling it to stop
name|ConsumerControl
name|stopConsumer
init|=
operator|new
name|ConsumerControl
argument_list|()
decl_stmt|;
name|stopConsumer
operator|.
name|setConsumerId
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|stopConsumer
operator|.
name|setClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|dispatchAsync
argument_list|(
name|stopConsumer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"slowConsumer abort ignored, no connection in context:"
operator|+
name|connectionContext
argument_list|)
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
name|info
argument_list|(
literal|"exception on stopping "
operator|+
operator|(
name|abortConnection
condition|?
literal|"connection"
else|:
literal|"consumer"
operator|)
operator|+
literal|" to abort slow consumer: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|long
name|getMaxSlowCount
parameter_list|()
block|{
return|return
name|maxSlowCount
return|;
block|}
comment|/**      * number of times a subscription can be deemed slow before triggering abort      * effect depends on dispatch rate as slow determination is done on dispatch      */
specifier|public
name|void
name|setMaxSlowCount
parameter_list|(
name|int
name|maxSlowCount
parameter_list|)
block|{
name|this
operator|.
name|maxSlowCount
operator|=
name|maxSlowCount
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxSlowDuration
parameter_list|()
block|{
return|return
name|maxSlowDuration
return|;
block|}
comment|/**      * time in milliseconds that a sub can remain slow before triggering      * an abort.      * @param maxSlowDuration      */
specifier|public
name|void
name|setMaxSlowDuration
parameter_list|(
name|long
name|maxSlowDuration
parameter_list|)
block|{
name|this
operator|.
name|maxSlowDuration
operator|=
name|maxSlowDuration
expr_stmt|;
block|}
specifier|public
name|long
name|getCheckPeriod
parameter_list|()
block|{
return|return
name|checkPeriod
return|;
block|}
comment|/**      * time in milliseconds between checks for slow subscriptions      * @param checkPeriod      */
specifier|public
name|void
name|setCheckPeriod
parameter_list|(
name|long
name|checkPeriod
parameter_list|)
block|{
name|this
operator|.
name|checkPeriod
operator|=
name|checkPeriod
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAbortConnection
parameter_list|()
block|{
return|return
name|abortConnection
return|;
block|}
comment|/**      * abort the consumers connection rather than sending a stop command to the remote consumer      * @param abortConnection      */
specifier|public
name|void
name|setAbortConnection
parameter_list|(
name|boolean
name|abortConnection
parameter_list|)
block|{
name|this
operator|.
name|abortConnection
operator|=
name|abortConnection
expr_stmt|;
block|}
specifier|static
class|class
name|SlowConsumerEntry
block|{
specifier|final
name|ConnectionContext
name|context
decl_stmt|;
name|int
name|slowCount
init|=
literal|1
decl_stmt|;
name|int
name|markCount
init|=
literal|0
decl_stmt|;
name|SlowConsumerEntry
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|void
name|slow
parameter_list|()
block|{
name|slowCount
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|markCount
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

