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
name|amqp
operator|.
name|protocol
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
name|transport
operator|.
name|amqp
operator|.
name|AmqpProtocolException
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
name|engine
operator|.
name|Delivery
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
name|engine
operator|.
name|Receiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|ByteArrayOutputStream
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

begin_comment
comment|/**  * Abstract base that provides common services for AMQP Receiver types.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AmqpAbstractReceiver
extends|extends
name|AmqpAbstractLink
argument_list|<
name|Receiver
argument_list|>
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
name|AmqpAbstractReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ByteArrayOutputStream
name|current
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|byte
index|[]
name|recvBuffer
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|8
index|]
decl_stmt|;
specifier|protected
specifier|final
name|int
name|configuredCredit
decl_stmt|;
comment|/**      * Handle create of new AMQP Receiver instance.      *      * @param session      *        the AmqpSession that servers as the parent of this Link.      * @param endpoint      *        the Receiver endpoint being managed by this class.      */
specifier|public
name|AmqpAbstractReceiver
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|Receiver
name|endpoint
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
name|this
operator|.
name|configuredCredit
operator|=
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getConfiguredReceiverCredit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|detach
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|flow
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Returns the amount of receiver credit that has been configured for this AMQP      * transport.  If no value was configured on the TransportConnector URI then a      * sensible default is used.      *      * @return the configured receiver credit to grant.      */
specifier|public
name|int
name|getConfiguredReceiverCredit
parameter_list|()
block|{
return|return
name|configuredCredit
return|;
block|}
comment|/**      * Provide the receiver endpoint with the given amount of credits.      *      * @param credits      *        the credit value to pass on to the wrapped Receiver.      */
specifier|public
name|void
name|flow
parameter_list|(
name|int
name|credits
parameter_list|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
name|credits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|delivery
parameter_list|(
name|Delivery
name|delivery
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|delivery
operator|.
name|isReadable
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delivery was not readable!"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|current
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
block|}
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|getEndpoint
argument_list|()
operator|.
name|recv
argument_list|(
name|recvBuffer
argument_list|,
literal|0
argument_list|,
name|recvBuffer
operator|.
name|length
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|current
operator|.
name|write
argument_list|(
name|recvBuffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|size
argument_list|()
operator|>
name|session
operator|.
name|getMaxFrameSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AmqpProtocolException
argument_list|(
literal|"Frame size of "
operator|+
name|current
operator|.
name|size
argument_list|()
operator|+
literal|" larger than max allowed "
operator|+
name|session
operator|.
name|getMaxFrameSize
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// Expecting more deliveries..
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return;
block|}
try|try
block|{
name|processDelivery
argument_list|(
name|delivery
argument_list|,
name|current
operator|.
name|toBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getEndpoint
argument_list|()
operator|.
name|advance
argument_list|()
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|void
name|processDelivery
parameter_list|(
name|Delivery
name|delivery
parameter_list|,
name|Buffer
name|deliveryBytes
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

