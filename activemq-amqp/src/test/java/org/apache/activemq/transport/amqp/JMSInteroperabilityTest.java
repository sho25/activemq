begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertTrue
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeFalse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
comment|/**  * Tests interoperability between OpenWire and AMQP  */
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
name|JMSInteroperabilityTest
extends|extends
name|JMSClientTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JMSInteroperabilityTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|transformer
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"Transformer->{0}"
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
literal|"jms"
block|}
block|,
block|{
literal|"native"
block|}
block|,
block|{
literal|"raw"
block|}
block|,             }
argument_list|)
return|;
block|}
specifier|public
name|JMSInteroperabilityTest
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
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAmqpTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
comment|//----- Tests for property handling between protocols --------------------//
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMessagePropertiesArePreservedOpenWireToAMQP
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|bool
init|=
literal|true
decl_stmt|;
name|byte
name|bValue
init|=
literal|127
decl_stmt|;
name|short
name|nShort
init|=
literal|10
decl_stmt|;
name|int
name|nInt
init|=
literal|5
decl_stmt|;
name|long
name|nLong
init|=
literal|333
decl_stmt|;
name|float
name|nFloat
init|=
literal|1
decl_stmt|;
name|double
name|nDouble
init|=
literal|100
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|propertyNames
init|=
literal|null
decl_stmt|;
name|String
name|testMessageBody
init|=
literal|"Testing msgPropertyExistTest"
decl_stmt|;
name|Connection
name|openwire
init|=
name|createJMSConnection
argument_list|()
decl_stmt|;
name|Connection
name|amqp
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|openwire
operator|.
name|start
argument_list|()
expr_stmt|;
name|amqp
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|openwireSession
init|=
name|openwire
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqp
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|queue
init|=
name|openwireSession
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|openwireProducer
init|=
name|openwireSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|amqpConsumer
init|=
name|amqpSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|outbound
init|=
name|openwireSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|outbound
operator|.
name|setText
argument_list|(
name|testMessageBody
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setBooleanProperty
argument_list|(
literal|"Boolean"
argument_list|,
name|bool
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setByteProperty
argument_list|(
literal|"Byte"
argument_list|,
name|bValue
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setShortProperty
argument_list|(
literal|"Short"
argument_list|,
name|nShort
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setIntProperty
argument_list|(
literal|"Integer"
argument_list|,
name|nInt
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setFloatProperty
argument_list|(
literal|"Float"
argument_list|,
name|nFloat
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setDoubleProperty
argument_list|(
literal|"Double"
argument_list|,
name|nDouble
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setStringProperty
argument_list|(
literal|"String"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setLongProperty
argument_list|(
literal|"Long"
argument_list|,
name|nLong
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setObjectProperty
argument_list|(
literal|"BooleanObject"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|bool
argument_list|)
argument_list|)
expr_stmt|;
name|openwireProducer
operator|.
name|send
argument_list|(
name|outbound
argument_list|)
expr_stmt|;
name|Message
name|inbound
init|=
name|amqpConsumer
operator|.
name|receive
argument_list|(
literal|2500
argument_list|)
decl_stmt|;
name|propertyNames
operator|=
name|inbound
operator|.
name|getPropertyNames
argument_list|()
expr_stmt|;
name|int
name|propertyCount
init|=
literal|0
decl_stmt|;
do|do
block|{
name|String
name|propertyName
init|=
name|propertyNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|"JMS"
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|propertyCount
operator|++
expr_stmt|;
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Boolean"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Byte"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Integer"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Short"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Float"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Double"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Long"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"BooleanObject"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Appclication Property set by client is: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Positive propertyExists test failed for {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
operator|+
literal|"1"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Negative propertyExists test failed for {} 1"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Negative propertyExists test failed for "
operator|+
name|propertyName
operator|+
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Appclication Property not set by client: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Appclication Property not set by client: "
operator|+
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"JMSProperty Name is: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|propertyNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
do|;
name|amqp
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwire
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of properties in received message."
argument_list|,
literal|9
argument_list|,
name|propertyCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMessagePropertiesArePreservedAMQPToOpenWire
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Raw Transformer doesn't expand message propeties.
name|assumeFalse
argument_list|(
name|transformer
operator|.
name|equals
argument_list|(
literal|"raw"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|bool
init|=
literal|true
decl_stmt|;
name|byte
name|bValue
init|=
literal|127
decl_stmt|;
name|short
name|nShort
init|=
literal|10
decl_stmt|;
name|int
name|nInt
init|=
literal|5
decl_stmt|;
name|long
name|nLong
init|=
literal|333
decl_stmt|;
name|float
name|nFloat
init|=
literal|1
decl_stmt|;
name|double
name|nDouble
init|=
literal|100
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|propertyNames
init|=
literal|null
decl_stmt|;
name|String
name|testMessageBody
init|=
literal|"Testing msgPropertyExistTest"
decl_stmt|;
name|Connection
name|openwire
init|=
name|createJMSConnection
argument_list|()
decl_stmt|;
name|Connection
name|amqp
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|openwire
operator|.
name|start
argument_list|()
expr_stmt|;
name|amqp
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|openwireSession
init|=
name|openwire
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqp
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|queue
init|=
name|openwireSession
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|amqpProducer
init|=
name|amqpSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|openwireConsumer
init|=
name|openwireSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|outbound
init|=
name|openwireSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|outbound
operator|.
name|setText
argument_list|(
name|testMessageBody
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setBooleanProperty
argument_list|(
literal|"Boolean"
argument_list|,
name|bool
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setByteProperty
argument_list|(
literal|"Byte"
argument_list|,
name|bValue
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setShortProperty
argument_list|(
literal|"Short"
argument_list|,
name|nShort
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setIntProperty
argument_list|(
literal|"Integer"
argument_list|,
name|nInt
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setFloatProperty
argument_list|(
literal|"Float"
argument_list|,
name|nFloat
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setDoubleProperty
argument_list|(
literal|"Double"
argument_list|,
name|nDouble
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setStringProperty
argument_list|(
literal|"String"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setLongProperty
argument_list|(
literal|"Long"
argument_list|,
name|nLong
argument_list|)
expr_stmt|;
name|outbound
operator|.
name|setObjectProperty
argument_list|(
literal|"BooleanObject"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|bool
argument_list|)
argument_list|)
expr_stmt|;
name|amqpProducer
operator|.
name|send
argument_list|(
name|outbound
argument_list|)
expr_stmt|;
name|Message
name|inbound
init|=
name|openwireConsumer
operator|.
name|receive
argument_list|(
literal|2500
argument_list|)
decl_stmt|;
name|propertyNames
operator|=
name|inbound
operator|.
name|getPropertyNames
argument_list|()
expr_stmt|;
name|int
name|propertyCount
init|=
literal|0
decl_stmt|;
do|do
block|{
name|String
name|propertyName
init|=
name|propertyNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|"JMS"
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|propertyCount
operator|++
expr_stmt|;
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Boolean"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Byte"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Integer"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Short"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Float"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Double"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"Long"
argument_list|)
operator|||
name|propertyName
operator|.
name|equals
argument_list|(
literal|"BooleanObject"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Appclication Property set by client is: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Positive propertyExists test failed for {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inbound
operator|.
name|propertyExists
argument_list|(
name|propertyName
operator|+
literal|"1"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Negative propertyExists test failed for {} 1"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Negative propertyExists test failed for "
operator|+
name|propertyName
operator|+
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Appclication Property not set by client: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Appclication Property not set by client: "
operator|+
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"JMSProperty Name is: {}"
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|propertyNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
do|;
name|amqp
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwire
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of properties in received message."
argument_list|,
literal|9
argument_list|,
name|propertyCount
argument_list|)
expr_stmt|;
block|}
comment|//----- Tests for OpenWire to Qpid JMS using MapMessage ------------------//
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testMapMessageSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|openwire
init|=
name|createJMSConnection
argument_list|()
decl_stmt|;
name|Connection
name|amqp
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|openwire
operator|.
name|start
argument_list|()
expr_stmt|;
name|amqp
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|openwireSession
init|=
name|openwire
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqp
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|queue
init|=
name|openwireSession
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|openwireProducer
init|=
name|openwireSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|amqpConsumer
init|=
name|amqpSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
comment|// Create the Message
name|ObjectMessage
name|outgoing
init|=
name|openwireSession
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|outgoingMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"none"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"long"
argument_list|,
literal|255L
argument_list|)
expr_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"empty-string"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"negative-int"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|outgoingMap
operator|.
name|put
argument_list|(
literal|"float"
argument_list|,
literal|0.12f
argument_list|)
expr_stmt|;
name|outgoing
operator|.
name|setObject
argument_list|(
name|outgoingMap
argument_list|)
expr_stmt|;
name|openwireProducer
operator|.
name|send
argument_list|(
name|outgoing
argument_list|)
expr_stmt|;
comment|// Now consumer the ObjectMessage
name|Message
name|received
init|=
name|amqpConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|ObjectMessage
name|incoming
init|=
operator|(
name|ObjectMessage
operator|)
name|received
decl_stmt|;
name|Object
name|incomingObject
init|=
name|incoming
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|incomingObject
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incomingObject
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|incomingMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|incomingObject
decl_stmt|;
name|assertEquals
argument_list|(
name|outgoingMap
operator|.
name|size
argument_list|()
argument_list|,
name|incomingMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|amqp
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwire
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//----- Tests for OpenWire to Qpid JMS using ObjectMessage ---------------//
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testObjectMessageContainingList
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|openwire
init|=
name|createJMSConnection
argument_list|()
decl_stmt|;
name|Connection
name|amqp
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|openwire
operator|.
name|start
argument_list|()
expr_stmt|;
name|amqp
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|openwireSession
init|=
name|openwire
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqp
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|queue
init|=
name|openwireSession
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|openwireProducer
init|=
name|openwireSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|amqpConsumer
init|=
name|amqpSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
comment|// Create the Message
name|ObjectMessage
name|outgoing
init|=
name|openwireSession
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|outgoingList
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
literal|255L
argument_list|)
expr_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|outgoingList
operator|.
name|add
argument_list|(
literal|0.12f
argument_list|)
expr_stmt|;
name|outgoing
operator|.
name|setObject
argument_list|(
name|outgoingList
argument_list|)
expr_stmt|;
name|openwireProducer
operator|.
name|send
argument_list|(
name|outgoing
argument_list|)
expr_stmt|;
comment|// Now consumer the ObjectMessage
name|Message
name|received
init|=
name|amqpConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|ObjectMessage
name|incoming
init|=
operator|(
name|ObjectMessage
operator|)
name|received
decl_stmt|;
name|Object
name|incomingObject
init|=
name|incoming
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|incomingObject
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incomingObject
operator|instanceof
name|List
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|incomingList
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|incomingObject
decl_stmt|;
name|assertEquals
argument_list|(
name|outgoingList
operator|.
name|size
argument_list|()
argument_list|,
name|incomingList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|amqp
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwire
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
