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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|assertNotNull
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
name|assertNull
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ProtocolException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|TimeUnit
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidClientIDException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|CredentialException
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
name|Broker
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
name|BrokerFilter
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
name|BrokerPlugin
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
name|ConnectionContext
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
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Message
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
name|QoS
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
name|Topic
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
name|Tracer
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
name|CONNACK
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
comment|/**  * Tests various use cases that require authentication or authorization over MQTT  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MQTTAuthTests
extends|extends
name|MQTTAuthTestSupport
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
name|MQTTAuthTests
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}: scheme({0})"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"mqtt"
block|,
literal|false
block|}
block|,
block|{
literal|"mqtt+ssl"
block|,
literal|true
block|}
block|,
block|{
literal|"mqtt+nio"
block|,
literal|false
block|}
block|,
block|{
literal|"mqtt+nio+ssl"
block|,
literal|true
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|MQTTAuthTests
parameter_list|(
name|String
name|connectorScheme
parameter_list|,
name|boolean
name|useSSL
parameter_list|)
block|{
name|super
argument_list|(
name|connectorScheme
argument_list|,
name|useSSL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testAnonymousUserConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setCleanSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setUserName
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setPassword
argument_list|(
operator|(
name|String
operator|)
literal|null
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Connected as anonymous client"
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
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testBadUserNameOrPasswordGetsConnAckWithErrorCode
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqttPub
init|=
name|createMQTTConnection
argument_list|(
literal|"pub"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mqttPub
operator|.
name|setUserName
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|mqttPub
operator|.
name|setPassword
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|mqttPub
operator|.
name|setTracer
argument_list|(
operator|new
name|Tracer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onReceive
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client received: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
if|if
condition|(
name|frame
operator|.
name|messageType
argument_list|()
operator|==
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
name|CONNACK
name|connAck
init|=
operator|new
name|CONNACK
argument_list|()
decl_stmt|;
try|try
block|{
name|connAck
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|connAck
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_NOT_AUTHORIZED
argument_list|,
name|connAck
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProtocolException
name|e
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Error decoding publish "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
name|err
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSend
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client sent: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|BlockingConnection
name|connectionPub
init|=
name|mqttPub
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connectionPub
operator|.
name|connect
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to connect."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|assertFalse
argument_list|(
literal|"connection should have failed."
argument_list|,
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testFailedSubscription
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|ANONYMOUS
init|=
literal|"anonymous"
decl_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setClientId
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
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
specifier|final
name|String
name|NAMED
init|=
literal|"named"
decl_stmt|;
name|byte
index|[]
name|qos
init|=
name|connection
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
name|NAMED
argument_list|,
name|QoS
operator|.
name|AT_MOST_ONCE
argument_list|)
block|,
operator|new
name|Topic
argument_list|(
name|ANONYMOUS
argument_list|,
name|QoS
operator|.
name|EXACTLY_ONCE
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|0x80
argument_list|,
name|qos
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|QoS
operator|.
name|EXACTLY_ONCE
operator|.
name|ordinal
argument_list|()
argument_list|,
name|qos
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// validate the subscription by sending a retained message
name|connection
operator|.
name|publish
argument_list|(
name|ANONYMOUS
argument_list|,
name|ANONYMOUS
operator|.
name|getBytes
argument_list|()
argument_list|,
name|QoS
operator|.
name|AT_MOST_ONCE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|connection
operator|.
name|receive
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ANONYMOUS
argument_list|,
operator|new
name|String
argument_list|(
name|msg
operator|.
name|getPayload
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|ack
argument_list|()
expr_stmt|;
name|connection
operator|.
name|unsubscribe
argument_list|(
operator|new
name|String
index|[]
block|{
name|ANONYMOUS
block|}
argument_list|)
expr_stmt|;
name|qos
operator|=
name|connection
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
name|ANONYMOUS
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|QoS
operator|.
name|AT_LEAST_ONCE
operator|.
name|ordinal
argument_list|()
argument_list|,
name|qos
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|msg
operator|=
name|connection
operator|.
name|receive
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ANONYMOUS
argument_list|,
operator|new
name|String
argument_list|(
name|msg
operator|.
name|getPayload
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|ack
argument_list|()
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testWildcardRetainedSubscription
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqttPub
init|=
name|createMQTTConnection
argument_list|(
literal|"pub"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mqttPub
operator|.
name|setUserName
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|mqttPub
operator|.
name|setPassword
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|BlockingConnection
name|connectionPub
init|=
name|mqttPub
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connectionPub
operator|.
name|connect
argument_list|()
expr_stmt|;
name|connectionPub
operator|.
name|publish
argument_list|(
literal|"one"
argument_list|,
literal|"test"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MQTT
name|mqttSub
init|=
name|createMQTTConnection
argument_list|(
literal|"sub"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mqttSub
operator|.
name|setUserName
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|mqttSub
operator|.
name|setPassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|BlockingConnection
name|connectionSub
init|=
name|mqttSub
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connectionSub
operator|.
name|connect
argument_list|()
expr_stmt|;
name|connectionSub
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
literal|"#"
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|connectionSub
operator|.
name|receive
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Shouldn't receive the message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testInvalidClientIdGetCorrectErrorCode
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqttPub
init|=
name|createMQTTConnection
argument_list|(
literal|"invalid"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|errorCode
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|mqttPub
operator|.
name|setTracer
argument_list|(
operator|new
name|Tracer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onReceive
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client received: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
if|if
condition|(
name|frame
operator|.
name|messageType
argument_list|()
operator|==
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
name|CONNACK
name|connAck
init|=
operator|new
name|CONNACK
argument_list|()
decl_stmt|;
try|try
block|{
name|connAck
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|connAck
argument_list|)
expr_stmt|;
name|errorCode
operator|.
name|set
argument_list|(
name|connAck
operator|.
name|code
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_IDENTIFIER_REJECTED
argument_list|,
name|connAck
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProtocolException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error decoding publish "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSend
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client sent: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|BlockingConnection
name|connectionPub
init|=
name|mqttPub
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connectionPub
operator|.
name|connect
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to connect."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_IDENTIFIER_REJECTED
operator|.
name|ordinal
argument_list|()
argument_list|,
name|errorCode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testBadCredentialExceptionGetsCorrectErrorCode
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqttPub
init|=
name|createMQTTConnection
argument_list|(
literal|"bad-credential"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mqttPub
operator|.
name|setUserName
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|mqttPub
operator|.
name|setPassword
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|errorCode
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|mqttPub
operator|.
name|setTracer
argument_list|(
operator|new
name|Tracer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onReceive
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client received: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
if|if
condition|(
name|frame
operator|.
name|messageType
argument_list|()
operator|==
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
name|CONNACK
name|connAck
init|=
operator|new
name|CONNACK
argument_list|()
decl_stmt|;
try|try
block|{
name|connAck
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|connAck
argument_list|)
expr_stmt|;
name|errorCode
operator|.
name|set
argument_list|(
name|connAck
operator|.
name|code
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD
argument_list|,
name|connAck
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProtocolException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error decoding publish "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSend
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client sent: {}"
argument_list|,
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|BlockingConnection
name|connectionPub
init|=
name|mqttPub
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connectionPub
operator|.
name|connect
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to connect."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD
operator|.
name|ordinal
argument_list|()
argument_list|,
name|errorCode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createPlugins
parameter_list|(
name|List
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerPlugin
name|failOnSpecificConditionsPlugin
init|=
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|clientId
init|=
name|info
operator|.
name|getClientId
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
operator|&&
operator|!
name|clientId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|clientId
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"invalid"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client ID was invalid"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidClientIDException
argument_list|(
literal|"Bad client Id"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|clientId
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"bad-credential"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"User Name was invalid"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CredentialException
argument_list|(
literal|"Unknwon User Name."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|plugins
operator|.
name|add
argument_list|(
name|failOnSpecificConditionsPlugin
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

