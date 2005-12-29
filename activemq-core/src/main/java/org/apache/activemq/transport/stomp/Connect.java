begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DataOutput
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
name|ConnectionInfo
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
name|ProducerInfo
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
name|Response
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
name|SessionInfo
import|;
end_import

begin_class
class|class
name|Connect
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
name|Connect
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
comment|// allow anyone to login for now
name|String
name|login
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Connect
operator|.
name|LOGIN
argument_list|)
decl_stmt|;
name|String
name|passcode
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Connect
operator|.
name|PASSCODE
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Connect
operator|.
name|CLIENT_ID
argument_list|)
decl_stmt|;
specifier|final
name|ConnectionInfo
name|connectionInfo
init|=
operator|new
name|ConnectionInfo
argument_list|()
decl_stmt|;
name|connectionInfo
operator|.
name|setConnectionId
argument_list|(
name|format
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
name|connectionInfo
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
else|else
name|connectionInfo
operator|.
name|setClientId
argument_list|(
literal|""
operator|+
name|connectionInfo
operator|.
name|getConnectionId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setUserName
argument_list|(
name|login
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setPassword
argument_list|(
name|passcode
argument_list|)
expr_stmt|;
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
return|return
operator|new
name|CommandEnvelope
argument_list|(
name|connectionInfo
argument_list|,
name|headers
argument_list|,
operator|new
name|ResponseListener
argument_list|()
block|{
specifier|public
name|boolean
name|onResponse
parameter_list|(
name|Response
name|receipt
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|receipt
operator|.
name|getCorrelationId
argument_list|()
operator|!=
name|connectionInfo
operator|.
name|getCommandId
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|SessionInfo
name|sessionInfo
init|=
operator|new
name|SessionInfo
argument_list|(
name|format
operator|.
name|getSessionId
argument_list|()
argument_list|)
decl_stmt|;
name|sessionInfo
operator|.
name|setCommandId
argument_list|(
name|format
operator|.
name|generateCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|sessionInfo
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|ProducerInfo
name|producerInfo
init|=
operator|new
name|ProducerInfo
argument_list|(
name|format
operator|.
name|getProducerId
argument_list|()
argument_list|)
decl_stmt|;
name|producerInfo
operator|.
name|setCommandId
argument_list|(
name|format
operator|.
name|generateCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|producerInfo
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|format
operator|.
name|addResponseListener
argument_list|(
operator|new
name|ResponseListener
argument_list|()
block|{
specifier|public
name|boolean
name|onResponse
parameter_list|(
name|Response
name|receipt
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|receipt
operator|.
name|getCorrelationId
argument_list|()
operator|!=
name|producerInfo
operator|.
name|getCommandId
argument_list|()
condition|)
return|return
literal|false
return|;
name|format
operator|.
name|onFullyConnected
argument_list|()
expr_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|CONNECTED
argument_list|)
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Connected
operator|.
name|SESSION
argument_list|)
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|SEPERATOR
argument_list|)
operator|.
name|append
argument_list|(
name|connectionInfo
operator|.
name|getClientId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|format
operator|.
name|addToPendingReadCommands
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|format
operator|.
name|addToPendingReadCommands
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

