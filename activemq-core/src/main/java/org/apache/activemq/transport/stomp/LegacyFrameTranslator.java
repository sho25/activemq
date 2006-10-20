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
name|stomp
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
name|ActiveMQBytesMessage
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
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
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
name|Destination
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Implements ActiveMQ 4.0 translations  */
end_comment

begin_class
specifier|public
class|class
name|LegacyFrameTranslator
implements|implements
name|FrameTranslator
block|{
specifier|public
name|ActiveMQMessage
name|convertFrame
parameter_list|(
name|StompFrame
name|command
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ProtocolException
block|{
specifier|final
name|Map
name|headers
init|=
name|command
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQMessage
name|msg
decl_stmt|;
if|if
condition|(
name|headers
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
condition|)
block|{
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
expr_stmt|;
name|ActiveMQBytesMessage
name|bm
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|bm
operator|.
name|writeBytes
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|=
name|bm
expr_stmt|;
block|}
else|else
block|{
name|ActiveMQTextMessage
name|text
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|text
operator|.
name|setText
argument_list|(
operator|new
name|String
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Text could not bet set: "
operator|+
name|e
argument_list|,
literal|false
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|msg
operator|=
name|text
expr_stmt|;
block|}
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromFrameToMessage
argument_list|(
name|command
argument_list|,
name|msg
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|msg
return|;
block|}
specifier|public
name|StompFrame
name|convertMessage
parameter_list|(
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|StompFrame
name|command
init|=
operator|new
name|StompFrame
argument_list|()
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
name|Map
name|headers
init|=
operator|new
name|HashMap
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|command
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQTextMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQBytesMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|msg
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|msg
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|,
literal|""
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|command
return|;
block|}
specifier|public
name|String
name|convertDestination
parameter_list|(
name|Destination
name|d
parameter_list|)
block|{
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ActiveMQDestination
name|amq_d
init|=
operator|(
name|ActiveMQDestination
operator|)
name|d
decl_stmt|;
name|String
name|p_name
init|=
name|amq_d
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|amq_d
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/queue/"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|amq_d
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/topic/"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|p_name
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|ActiveMQDestination
name|convertDestination
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ProtocolException
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/queue/"
argument_list|)
condition|)
block|{
name|String
name|q_name
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/queue/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|q_name
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/topic/"
argument_list|)
condition|)
block|{
name|String
name|t_name
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/topic/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|t_name
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Illegal destination name: ["
operator|+
name|name
operator|+
literal|"] -- ActiveMQ STOMP destinations "
operator|+
literal|"must begine with /queue/ or /topic/"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

