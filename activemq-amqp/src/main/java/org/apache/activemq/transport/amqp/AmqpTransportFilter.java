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
name|BrokerContext
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
name|Command
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
name|Transport
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
name|TransportFilter
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
name|TransportListener
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
name|InboundTransformer
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
name|tcp
operator|.
name|SslTransport
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
name|util
operator|.
name|IOExceptionSupport
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
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_comment
comment|/**  * The AMQPTransportFilter normally sits on top of a TcpTransport that has been  * configured with the StompWireFormat and is used to convert AMQP commands to  * ActiveMQ commands. All of the conversion work is done by delegating to the  * AMQPProtocolConverter  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransportFilter
extends|extends
name|TransportFilter
implements|implements
name|AmqpTransport
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
name|AmqpTransportFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|TRACE
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpTransportFilter
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".AMQPIO"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AmqpProtocolConverter
name|protocolConverter
decl_stmt|;
comment|//    private AmqpInactivityMonitor monitor;
specifier|private
name|AmqpWireFormat
name|wireFormat
decl_stmt|;
specifier|private
name|boolean
name|trace
decl_stmt|;
specifier|private
name|String
name|transformer
init|=
name|InboundTransformer
operator|.
name|TRANSFORMER_NATIVE
decl_stmt|;
specifier|public
name|AmqpTransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|,
name|BrokerContext
name|brokerContext
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|protocolConverter
operator|=
operator|new
name|AmqpProtocolConverter
argument_list|(
name|this
argument_list|,
name|brokerContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|wireFormat
operator|instanceof
name|AmqpWireFormat
condition|)
block|{
name|this
operator|.
name|wireFormat
operator|=
operator|(
name|AmqpWireFormat
operator|)
name|wireFormat
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|protocolConverter
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|protocolConverter
operator|.
name|onActiveMQCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|protocolConverter
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|protocolConverter
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|protocolConverter
operator|.
name|onAMQPException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|protocolConverter
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|trace
condition|)
block|{
name|TRACE
operator|.
name|trace
argument_list|(
literal|"Received: \n"
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|protocolConverter
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|protocolConverter
operator|.
name|onAMQPData
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|protocolConverter
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sendToActiveMQ
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
assert|assert
name|protocolConverter
operator|.
name|lock
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
name|TransportListener
name|l
init|=
name|transportListener
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sendToAmqp
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|protocolConverter
operator|.
name|lock
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
if|if
condition|(
name|trace
condition|)
block|{
name|TRACE
operator|.
name|trace
argument_list|(
literal|"Sending: \n"
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|Transport
name|n
init|=
name|next
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|X509Certificate
index|[]
name|getPeerCertificates
parameter_list|()
block|{
if|if
condition|(
name|next
operator|instanceof
name|SslTransport
condition|)
block|{
name|X509Certificate
index|[]
name|peerCerts
init|=
operator|(
operator|(
name|SslTransport
operator|)
name|next
operator|)
operator|.
name|getPeerCertificates
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
operator|&&
name|peerCerts
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Peer Identity has been verified\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|peerCerts
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
comment|//    @Override
comment|//    public AmqpInactivityMonitor getInactivityMonitor() {
comment|//        return monitor;
comment|//    }
comment|//
comment|//    public void setInactivityMonitor(AmqpInactivityMonitor monitor) {
comment|//        this.monitor = monitor;
comment|//    }
annotation|@
name|Override
specifier|public
name|AmqpWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|this
operator|.
name|wireFormat
return|;
block|}
specifier|public
name|void
name|handleException
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|super
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
specifier|public
name|void
name|setTransformer
parameter_list|(
name|String
name|transformer
parameter_list|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
block|}
end_class

end_unit

