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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|TransportFilter
implements|implements
name|TransportListener
implements|,
name|Transport
block|{
specifier|protected
specifier|final
name|Transport
name|next
decl_stmt|;
specifier|protected
name|TransportListener
name|transportListener
decl_stmt|;
specifier|public
name|TransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|channelListener
parameter_list|)
block|{
name|this
operator|.
name|transportListener
operator|=
name|channelListener
expr_stmt|;
if|if
condition|(
name|channelListener
operator|==
literal|null
condition|)
block|{
name|next
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|next
operator|.
name|setTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.activemq.Service#start()      * @throws IOException      *             if the next channel has not been set.      */
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The next channel has not been set."
argument_list|)
throw|;
block|}
if|if
condition|(
name|transportListener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The command listener has not been set."
argument_list|)
throw|;
block|}
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.Service#stop()      */
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the next.      */
specifier|public
name|Transport
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Object
name|command
parameter_list|,
name|ResponseCallback
name|responseCallback
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|asyncRequest
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|request
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|request
argument_list|(
name|command
argument_list|,
name|timeout
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|transportListener
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{
name|transportListener
operator|.
name|transportInterupted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{
name|transportListener
operator|.
name|transportResumed
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|narrow
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|target
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
name|next
operator|.
name|narrow
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|next
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
comment|/**      * @return      * @see org.apache.activemq.transport.Transport#isFaultTolerant()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
name|next
operator|.
name|isFaultTolerant
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|next
operator|.
name|isDisposed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|next
operator|.
name|isConnected
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reconnect
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|reconnect
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
return|return
name|next
operator|.
name|getReceiveCounter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReconnectSupported
parameter_list|()
block|{
return|return
name|next
operator|.
name|isReconnectSupported
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUpdateURIsSupported
parameter_list|()
block|{
return|return
name|next
operator|.
name|isUpdateURIsSupported
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateURIs
parameter_list|(
name|boolean
name|rebalance
parameter_list|,
name|URI
index|[]
name|uris
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|updateURIs
argument_list|(
name|rebalance
argument_list|,
name|uris
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getPeerCertificates
parameter_list|()
block|{
return|return
name|next
operator|.
name|getPeerCertificates
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPeerCertificates
parameter_list|(
name|X509Certificate
index|[]
name|certificates
parameter_list|)
block|{
name|next
operator|.
name|setPeerCertificates
argument_list|(
name|certificates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|WireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|next
operator|.
name|getWireFormat
argument_list|()
return|;
block|}
block|}
end_class

end_unit

