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
name|stomp
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
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
comment|/**  * The StompTransportFilter normally sits on top of a TcpTransport that has been  * configured with the StompWireFormat and is used to convert STOMP commands to  * ActiveMQ commands. All of the conversion work is done by delegating to the  * ProtocolConverter.  *   * @author<a href="http://hiramchirino.com">chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|StompTransportFilter
extends|extends
name|TransportFilter
implements|implements
name|StompTransport
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
name|StompTransportFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ProtocolConverter
name|protocolConverter
decl_stmt|;
specifier|private
specifier|final
name|FrameTranslator
name|frameTranslator
decl_stmt|;
specifier|private
name|boolean
name|trace
decl_stmt|;
specifier|public
name|StompTransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|,
name|FrameTranslator
name|translator
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
name|frameTranslator
operator|=
name|translator
expr_stmt|;
name|this
operator|.
name|protocolConverter
operator|=
operator|new
name|ProtocolConverter
argument_list|(
name|this
argument_list|,
name|translator
argument_list|,
name|brokerContext
argument_list|)
expr_stmt|;
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
name|onActiveMQCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
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
name|LOG
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
name|onStompCommand
argument_list|(
operator|(
name|StompFrame
operator|)
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
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
name|sendToStomp
parameter_list|(
name|StompFrame
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|trace
condition|)
block|{
name|LOG
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
name|FrameTranslator
name|getFrameTranslator
parameter_list|()
block|{
return|return
name|frameTranslator
return|;
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
block|}
end_class

end_unit

