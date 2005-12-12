begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|command
operator|.
name|ConsumerInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
name|Properties
import|;
end_import

begin_class
class|class
name|Subscribe
implements|implements
name|StompCommand
block|{
specifier|private
name|HeaderParser
name|headerParser
init|=
operator|new
name|HeaderParser
argument_list|()
decl_stmt|;
specifier|private
name|StompWireFormat
name|format
decl_stmt|;
name|Subscribe
parameter_list|(
name|StompWireFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|commandLine
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ConsumerInfo
name|ci
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|Properties
name|headers
init|=
name|headerParser
operator|.
name|parse
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|destination
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|DESTINATION
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|actual_dest
init|=
name|DestinationNamer
operator|.
name|convert
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ci
operator|.
name|setDestination
argument_list|(
name|DestinationNamer
operator|.
name|convert
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|ConsumerId
name|consumerId
init|=
name|format
operator|.
name|createConsumerId
argument_list|()
decl_stmt|;
name|ci
operator|.
name|setConsumerId
argument_list|(
name|consumerId
argument_list|)
expr_stmt|;
name|ci
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// ci.setSessionId(format.getSessionId());
while|while
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
condition|)
block|{         }
name|String
name|subscriptionId
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|ID
argument_list|,
name|Subscription
operator|.
name|NO_ID
argument_list|)
decl_stmt|;
name|Subscription
name|s
init|=
operator|new
name|Subscription
argument_list|(
name|format
argument_list|,
name|consumerId
argument_list|,
name|subscriptionId
argument_list|)
decl_stmt|;
name|s
operator|.
name|setDestination
argument_list|(
name|actual_dest
argument_list|)
expr_stmt|;
name|String
name|ack_mode_key
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|ACK_MODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ack_mode_key
operator|!=
literal|null
operator|&&
name|ack_mode_key
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|CLIENT
argument_list|)
condition|)
block|{
name|s
operator|.
name|setAckMode
argument_list|(
name|Subscription
operator|.
name|CLIENT_ACK
argument_list|)
expr_stmt|;
block|}
name|format
operator|.
name|addSubscription
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandEnvelope
argument_list|(
name|ci
argument_list|,
name|headers
argument_list|)
return|;
block|}
block|}
end_class

end_unit

