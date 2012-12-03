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
name|amqp
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
name|qpid
operator|.
name|proton
operator|.
name|jms
operator|.
name|JMSVendor
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQJMSVendor
extends|extends
name|JMSVendor
block|{
specifier|final
specifier|public
specifier|static
name|ActiveMQJMSVendor
name|INSTANCE
init|=
operator|new
name|ActiveMQJMSVendor
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQJMSVendor
parameter_list|()
block|{}
annotation|@
name|Override
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQBytesMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQStreamMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|createMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTextMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQObjectMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMapMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|super
operator|.
name|createDestination
argument_list|(
name|name
argument_list|,
name|Destination
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
extends|extends
name|Destination
parameter_list|>
name|T
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|kind
parameter_list|)
block|{
if|if
condition|(
name|kind
operator|==
name|Queue
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|Topic
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|TemporaryQueue
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTempQueue
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|TemporaryTopic
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTempTopic
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
return|return
name|kind
operator|.
name|cast
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|name
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXUserID
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setUserID
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXGroupID
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setGroupID
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXGroupSequence
parameter_list|(
name|Message
name|msg
parameter_list|,
name|int
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setGroupSequence
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXDeliveryCount
parameter_list|(
name|Message
name|msg
parameter_list|,
name|long
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setRedeliveryCounter
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toAddress
parameter_list|(
name|Destination
name|dest
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|dest
operator|)
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

