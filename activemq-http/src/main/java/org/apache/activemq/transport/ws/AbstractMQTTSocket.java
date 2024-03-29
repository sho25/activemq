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
name|ws
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
name|Map
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
name|CountDownLatch
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
name|locks
operator|.
name|ReentrantLock
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
name|broker
operator|.
name|BrokerServiceAware
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
name|jms
operator|.
name|pool
operator|.
name|IntrospectionSupport
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
name|TransportSupport
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
name|mqtt
operator|.
name|MQTTInactivityMonitor
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
name|mqtt
operator|.
name|MQTTProtocolConverter
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
name|mqtt
operator|.
name|MQTTTransport
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
name|mqtt
operator|.
name|MQTTWireFormat
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
name|util
operator|.
name|ServiceStopper
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
name|MQTTFrame
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMQTTSocket
extends|extends
name|TransportSupport
implements|implements
name|MQTTTransport
implements|,
name|BrokerServiceAware
block|{
specifier|protected
name|ReentrantLock
name|protocolLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|protected
specifier|volatile
name|MQTTProtocolConverter
name|protocolConverter
init|=
literal|null
decl_stmt|;
specifier|protected
name|MQTTWireFormat
name|wireFormat
init|=
operator|new
name|MQTTWireFormat
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|MQTTInactivityMonitor
name|mqttInactivityMonitor
init|=
operator|new
name|MQTTInactivityMonitor
argument_list|(
name|this
argument_list|,
name|wireFormat
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|CountDownLatch
name|socketTransportStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
specifier|volatile
name|int
name|receiveCounter
decl_stmt|;
specifier|protected
specifier|final
name|String
name|remoteAddress
decl_stmt|;
specifier|protected
name|X509Certificate
index|[]
name|peerCertificates
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
decl_stmt|;
specifier|public
name|AbstractMQTTSocket
parameter_list|(
name|String
name|remoteAddress
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|remoteAddress
operator|=
name|remoteAddress
expr_stmt|;
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
name|protocolLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|getProtocolConverter
argument_list|()
operator|.
name|onActiveMQCommand
argument_list|(
operator|(
name|Command
operator|)
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
finally|finally
block|{
name|protocolLock
operator|.
name|unlock
argument_list|()
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
name|protocolLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|protocolLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
name|mqttInactivityMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|handleStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|socketTransportStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|mqttInactivityMonitor
operator|.
name|setTransportListener
argument_list|(
name|getTransportListener
argument_list|()
argument_list|)
expr_stmt|;
name|mqttInactivityMonitor
operator|.
name|startConnectChecker
argument_list|(
name|wireFormat
operator|.
name|getConnectAttemptTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//----- Abstract methods for subclasses to implement ---------------------//
annotation|@
name|Override
specifier|public
specifier|abstract
name|void
name|sendToMQTT
parameter_list|(
name|MQTTFrame
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called when the transport is stopping to allow the dervied classes      * a chance to close WebSocket resources.      *      * @throws IOException if an error occurs during the stop.      */
specifier|public
specifier|abstract
name|void
name|handleStopped
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|//----- Accessor methods -------------------------------------------------//
annotation|@
name|Override
specifier|public
name|MQTTInactivityMonitor
name|getInactivityMonitor
parameter_list|()
block|{
return|return
name|mqttInactivityMonitor
return|;
block|}
annotation|@
name|Override
specifier|public
name|MQTTWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|wireFormat
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
name|remoteAddress
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
return|return
name|receiveCounter
return|;
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
name|peerCertificates
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
name|this
operator|.
name|peerCertificates
operator|=
name|certificates
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
comment|//----- Internal support methods -----------------------------------------//
specifier|protected
name|MQTTProtocolConverter
name|getProtocolConverter
parameter_list|()
block|{
if|if
condition|(
name|protocolConverter
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|protocolConverter
operator|==
literal|null
condition|)
block|{
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
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|protocolConverter
argument_list|,
name|transportOptions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|protocolConverter
return|;
block|}
specifier|protected
name|boolean
name|transportStartedAtLeastOnce
parameter_list|()
block|{
return|return
name|socketTransportStarted
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|public
name|void
name|setTransportOptions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
parameter_list|)
block|{
name|this
operator|.
name|transportOptions
operator|=
name|transportOptions
expr_stmt|;
block|}
block|}
end_class

end_unit

