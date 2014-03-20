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
name|mqtt
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|BrokerService
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
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|*
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
comment|/**  * The MQTTTransportFilter normally sits on top of a TcpTransport that has been  * configured with the StompWireFormat and is used to convert MQTT commands to  * ActiveMQ commands. All of the conversion work is done by delegating to the  * MQTTProtocolConverter  */
end_comment

begin_class
specifier|public
class|class
name|MQTTTransportFilter
extends|extends
name|TransportFilter
implements|implements
name|MQTTTransport
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
name|MQTTTransportFilter
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
name|MQTTTransportFilter
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".MQTTIO"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MQTTProtocolConverter
name|protocolConverter
decl_stmt|;
specifier|private
name|MQTTInactivityMonitor
name|monitor
decl_stmt|;
specifier|private
name|MQTTWireFormat
name|wireFormat
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|trace
decl_stmt|;
specifier|private
specifier|final
name|Object
name|sendLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|MQTTTransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|,
name|BrokerService
name|brokerService
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
name|MQTTProtocolConverter
argument_list|(
name|this
argument_list|,
name|brokerService
argument_list|)
expr_stmt|;
if|if
condition|(
name|wireFormat
operator|instanceof
name|MQTTWireFormat
condition|)
block|{
name|this
operator|.
name|wireFormat
operator|=
operator|(
name|MQTTWireFormat
operator|)
name|wireFormat
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
try|try
block|{
name|MQTTFrame
name|frame
init|=
operator|(
name|MQTTFrame
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|trace
condition|)
block|{
name|TRACE
operator|.
name|trace
argument_list|(
literal|"Received: "
operator|+
name|toString
argument_list|(
name|frame
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|protocolConverter
operator|.
name|onMQTTCommand
argument_list|(
name|frame
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|sendToMQTT
parameter_list|(
name|MQTTFrame
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
condition|)
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
literal|"Sending : "
operator|+
name|toString
argument_list|(
name|command
argument_list|)
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
comment|// sync access to underlying transport buffer
synchronized|synchronized
init|(
name|sendLock
init|)
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
block|}
block|}
specifier|static
specifier|private
name|String
name|toString
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
if|if
condition|(
name|frame
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
switch|switch
condition|(
name|frame
operator|.
name|messageType
argument_list|()
condition|)
block|{
case|case
name|PINGREQ
operator|.
name|TYPE
case|:
return|return
operator|new
name|PINGREQ
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PINGRESP
operator|.
name|TYPE
case|:
return|return
operator|new
name|PINGRESP
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|CONNECT
operator|.
name|TYPE
case|:
return|return
operator|new
name|CONNECT
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|DISCONNECT
operator|.
name|TYPE
case|:
return|return
operator|new
name|DISCONNECT
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|SUBSCRIBE
operator|.
name|TYPE
case|:
return|return
operator|new
name|SUBSCRIBE
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|UNSUBSCRIBE
operator|.
name|TYPE
case|:
return|return
operator|new
name|UNSUBSCRIBE
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PUBLISH
operator|.
name|TYPE
case|:
return|return
operator|new
name|PUBLISH
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PUBACK
operator|.
name|TYPE
case|:
return|return
operator|new
name|PUBACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PUBREC
operator|.
name|TYPE
case|:
return|return
operator|new
name|PUBREC
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PUBREL
operator|.
name|TYPE
case|:
return|return
operator|new
name|PUBREL
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|PUBCOMP
operator|.
name|TYPE
case|:
return|return
operator|new
name|PUBCOMP
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|CONNACK
operator|.
name|TYPE
case|:
return|return
operator|new
name|CONNACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|SUBACK
operator|.
name|TYPE
case|:
return|return
operator|new
name|SUBACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
operator|.
name|toString
argument_list|()
return|;
default|default:
return|return
name|frame
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|frame
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|stopped
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|MQTTInactivityMonitor
name|getInactivityMonitor
parameter_list|()
block|{
return|return
name|monitor
return|;
block|}
specifier|public
name|void
name|setInactivityMonitor
parameter_list|(
name|MQTTInactivityMonitor
name|monitor
parameter_list|)
block|{
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MQTTWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|this
operator|.
name|wireFormat
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
name|protocolConverter
operator|.
name|onTransportError
argument_list|()
expr_stmt|;
name|super
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getDefaultKeepAlive
parameter_list|()
block|{
return|return
name|protocolConverter
operator|!=
literal|null
condition|?
name|protocolConverter
operator|.
name|getDefaultKeepAlive
argument_list|()
else|:
operator|-
literal|1
return|;
block|}
specifier|public
name|void
name|setDefaultKeepAlive
parameter_list|(
name|long
name|defaultHeartBeat
parameter_list|)
block|{
name|protocolConverter
operator|.
name|setDefaultKeepAlive
argument_list|(
name|defaultHeartBeat
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getActiveMQSubscriptionPrefetch
parameter_list|()
block|{
return|return
name|protocolConverter
operator|.
name|getActiveMQSubscriptionPrefetch
argument_list|()
return|;
block|}
comment|/**      * set the default prefetch size when mapping the MQTT subscription to an ActiveMQ one      * The default = 1      * @param activeMQSubscriptionPrefetch set the prefetch for the corresponding ActiveMQ subscription      */
specifier|public
name|void
name|setActiveMQSubscriptionPrefetch
parameter_list|(
name|int
name|activeMQSubscriptionPrefetch
parameter_list|)
block|{
name|protocolConverter
operator|.
name|setActiveMQSubscriptionPrefetch
argument_list|(
name|activeMQSubscriptionPrefetch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

