begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|logwriters
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BaseCommand
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
name|MessageAck
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
name|MessageDispatch
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
name|ProducerAck
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
name|ProducerId
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
name|WireFormatInfo
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
name|LogWriter
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

begin_comment
comment|/**  * Custom implementation of LogWriter interface.  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  *   */
end_comment

begin_class
specifier|public
class|class
name|CustomLogWriter
implements|implements
name|LogWriter
block|{
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|initialMessage
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{              }
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logRequest
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ SENDREQ: "
operator|+
name|CustomLogWriter
operator|.
name|commandToString
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logResponse
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Object
name|response
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ GOT_RESPONSE: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logAsyncRequest
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ SENDING_ASNYC_REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logOneWay
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ SENDING: "
operator|+
name|CustomLogWriter
operator|.
name|commandToString
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logReceivedCommand
parameter_list|(
name|Logger
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ RECEIVED: "
operator|+
name|CustomLogWriter
operator|.
name|commandToString
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logReceivedException
parameter_list|(
name|Logger
name|log
parameter_list|,
name|IOException
name|error
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"$$ RECEIVED_EXCEPTION: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**      * Transforms a command into a String      * @param command An object (hopefully of the BaseCommand class or subclass)      * to be transformed into String.      * @return A String which will be written by the CustomLogWriter.      * If the object is not a BaseCommand, the String       * "Unrecognized_object " + command.toString()      * will be returned.      */
specifier|private
specifier|static
name|String
name|commandToString
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|command
operator|instanceof
name|BaseCommand
condition|)
block|{
name|BaseCommand
name|bc
init|=
operator|(
name|BaseCommand
operator|)
name|command
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|command
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|bc
operator|.
name|isResponseRequired
argument_list|()
condition|?
literal|'T'
else|:
literal|'F'
argument_list|)
expr_stmt|;
name|Message
name|m
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|bc
operator|instanceof
name|Message
condition|)
block|{
name|m
operator|=
operator|(
name|Message
operator|)
name|bc
expr_stmt|;
block|}
if|if
condition|(
name|bc
operator|instanceof
name|MessageDispatch
condition|)
block|{
name|m
operator|=
operator|(
operator|(
name|MessageDispatch
operator|)
name|bc
operator|)
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|m
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|ProducerId
name|pid
init|=
name|m
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
name|long
name|sid
init|=
name|pid
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|pid
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sid
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|pid
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|m
operator|.
name|getCorrelationId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|m
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bc
operator|instanceof
name|MessageDispatch
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" toConsumer:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
operator|(
name|MessageDispatch
operator|)
name|bc
operator|)
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bc
operator|instanceof
name|ProducerAck
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" ProducerId:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
operator|(
name|ProducerAck
operator|)
name|bc
operator|)
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bc
operator|instanceof
name|MessageAck
condition|)
block|{
name|MessageAck
name|ma
init|=
operator|(
name|MessageAck
operator|)
name|bc
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" ConsumerID:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ma
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" ack:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ma
operator|.
name|getFirstMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ma
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bc
operator|instanceof
name|ConnectionInfo
condition|)
block|{
name|ConnectionInfo
name|ci
init|=
operator|(
name|ConnectionInfo
operator|)
name|bc
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ci
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|command
operator|instanceof
name|WireFormatInfo
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"WireFormatInfo"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"Unrecognized_object "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|command
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

