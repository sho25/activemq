begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
operator|.
name|test
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
name|Message
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
name|MessageId
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
name|MessageRecoveryListener
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
name|MessageStore
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|ReplicationTestSupport
block|{
specifier|static
name|long
name|id_counter
init|=
literal|0L
decl_stmt|;
specifier|static
name|String
name|payload
init|=
literal|""
decl_stmt|;
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
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|payload
operator|+=
literal|"x"
expr_stmt|;
block|}
block|}
specifier|static
specifier|public
name|ActiveMQTextMessage
name|addMessage
parameter_list|(
name|MessageStore
name|ms
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"id"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|id_counter
operator|+=
literal|1
expr_stmt|;
name|MessageId
name|messageId
init|=
operator|new
name|MessageId
argument_list|(
literal|"ID:localhost-56913-1254499826208-0:0:1:1:"
operator|+
name|id_counter
argument_list|)
decl_stmt|;
name|messageId
operator|.
name|setBrokerSequenceId
argument_list|(
name|id_counter
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|ms
operator|.
name|addMessage
argument_list|(
operator|new
name|ConnectionContext
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|static
specifier|public
name|ArrayList
argument_list|<
name|String
argument_list|>
name|getMessages
parameter_list|(
name|MessageStore
name|ms
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ms
operator|.
name|recover
argument_list|(
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|rc
operator|.
name|add
argument_list|(
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|message
operator|)
operator|.
name|getStringProperty
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit
