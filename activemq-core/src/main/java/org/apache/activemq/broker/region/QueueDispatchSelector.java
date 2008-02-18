begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|group
operator|.
name|MessageGroupMap
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
name|SimpleDispatchSelector
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
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
name|ConsumerId
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
name|Message
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
comment|/**  * Queue dispatch policy that determines if a message can be sent to a subscription  *   * @org.apache.xbean.XBean  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|QueueDispatchSelector
extends|extends
name|SimpleDispatchSelector
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
name|QueueDispatchSelector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Subscription
name|exclusiveConsumer
decl_stmt|;
comment|/**      * @param destination      */
specifier|public
name|QueueDispatchSelector
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Subscription
name|getExclusiveConsumer
parameter_list|()
block|{
return|return
name|exclusiveConsumer
return|;
block|}
specifier|public
name|void
name|setExclusiveConsumer
parameter_list|(
name|Subscription
name|exclusiveConsumer
parameter_list|)
block|{
name|this
operator|.
name|exclusiveConsumer
operator|=
name|exclusiveConsumer
expr_stmt|;
block|}
specifier|public
name|boolean
name|isExclusiveConsumer
parameter_list|(
name|Subscription
name|s
parameter_list|)
block|{
return|return
name|s
operator|==
name|this
operator|.
name|exclusiveConsumer
return|;
block|}
specifier|public
name|boolean
name|canSelect
parameter_list|(
name|Subscription
name|subscription
parameter_list|,
name|MessageReference
name|m
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|subscription
operator|.
name|isBrowser
argument_list|()
operator|&&
name|super
operator|.
name|canDispatch
argument_list|(
name|subscription
argument_list|,
name|m
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|result
init|=
name|super
operator|.
name|canDispatch
argument_list|(
name|subscription
argument_list|,
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|result
operator|=
name|exclusiveConsumer
operator|==
literal|null
operator|||
name|exclusiveConsumer
operator|==
name|subscription
expr_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|QueueMessageReference
name|node
init|=
operator|(
name|QueueMessageReference
operator|)
name|m
decl_stmt|;
comment|// Keep message groups together.
name|String
name|groupId
init|=
name|node
operator|.
name|getGroupID
argument_list|()
decl_stmt|;
name|int
name|sequence
init|=
name|node
operator|.
name|getGroupSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupId
operator|!=
literal|null
condition|)
block|{
name|MessageGroupMap
name|messageGroupOwners
init|=
operator|(
operator|(
name|Queue
operator|)
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|)
operator|.
name|getMessageGroupOwners
argument_list|()
decl_stmt|;
comment|// If we can own the first, then no-one else should own the
comment|// rest.
if|if
condition|(
name|sequence
operator|==
literal|1
condition|)
block|{
name|assignGroup
argument_list|(
name|subscription
argument_list|,
name|messageGroupOwners
argument_list|,
name|node
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Make sure that the previous owner is still valid, we may
comment|// need to become the new owner.
name|ConsumerId
name|groupOwner
decl_stmt|;
name|groupOwner
operator|=
name|messageGroupOwners
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupOwner
operator|==
literal|null
condition|)
block|{
name|assignGroup
argument_list|(
name|subscription
argument_list|,
name|messageGroupOwners
argument_list|,
name|node
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|groupOwner
operator|.
name|equals
argument_list|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
block|{
comment|// A group sequence< 1 is an end of group signal.
if|if
condition|(
name|sequence
operator|<
literal|0
condition|)
block|{
name|messageGroupOwners
operator|.
name|removeGroup
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|void
name|assignGroup
parameter_list|(
name|Subscription
name|subs
parameter_list|,
name|MessageGroupMap
name|messageGroupOwners
parameter_list|,
name|MessageReference
name|n
parameter_list|,
name|String
name|groupId
parameter_list|)
throws|throws
name|IOException
block|{
name|messageGroupOwners
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
name|subs
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|n
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|ActiveMQMessage
condition|)
block|{
name|ActiveMQMessage
name|activeMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
try|try
block|{
name|activeMessage
operator|.
name|setBooleanProperty
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
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
literal|"Failed to set boolean header: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

