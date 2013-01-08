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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|Wait
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
name|client
operator|.
name|BlockingConnection
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
name|client
operator|.
name|MQTT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|MQTTTest
extends|extends
name|AbstractMQTTTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testPingKeepsInactivityMonitorAlive
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setKeepAlive
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|BlockingConnection
name|connection
init|=
name|mqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"KeepAlive didn't work properly"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTurnOffInactivityMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|(
literal|"?transport.useInactivityMonitor=false"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setKeepAlive
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|BlockingConnection
name|connection
init|=
name|mqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"KeepAlive didn't work properly"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultKeepAliveWhenClientSpecifiesZero
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default keep alive in milliseconds
name|addMQTTConnector
argument_list|(
literal|"?transport.defaultKeepAlive=2000"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setKeepAlive
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|BlockingConnection
name|connection
init|=
name|mqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"KeepAlive didn't work properly"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
literal|"mqtt"
return|;
block|}
specifier|protected
name|void
name|addMQTTConnector
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addMQTTConnector
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|mqttConnector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|getProtocolScheme
argument_list|()
operator|+
literal|"://localhost:0"
operator|+
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|MQTTClientProvider
name|getMQTTClientProvider
parameter_list|()
block|{
return|return
operator|new
name|FuseMQQTTClientProvider
argument_list|()
return|;
block|}
specifier|protected
name|MQTT
name|createMQTTConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|,
name|mqttConnector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
comment|// shut off connect retry
name|mqtt
operator|.
name|setConnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setReconnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
block|}
end_class

end_unit

