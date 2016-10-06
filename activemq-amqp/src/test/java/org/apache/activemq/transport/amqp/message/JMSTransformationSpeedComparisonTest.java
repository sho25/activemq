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
operator|.
name|message
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|HashMap
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
name|TimeUnit
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
name|ActiveMQMessage
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
name|amqp
operator|.
name|JMSInteroperabilityTest
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
name|Proton
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
name|amqp
operator|.
name|Symbol
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
name|amqp
operator|.
name|messaging
operator|.
name|AmqpValue
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
name|amqp
operator|.
name|messaging
operator|.
name|ApplicationProperties
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
name|amqp
operator|.
name|messaging
operator|.
name|MessageAnnotations
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
name|codec
operator|.
name|CompositeWritableBuffer
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
name|codec
operator|.
name|DroppingWritableBuffer
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
name|codec
operator|.
name|WritableBuffer
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
name|message
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
name|qpid
operator|.
name|proton
operator|.
name|message
operator|.
name|ProtonJMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|TestName
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
comment|/**  * Some simple performance tests for the Message Transformers.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Enable for profiling"
argument_list|)
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|JMSTransformationSpeedComparisonTest
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
annotation|@
name|Rule
specifier|public
name|TestName
name|test
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|transformer
decl_stmt|;
specifier|private
specifier|final
name|int
name|WARM_CYCLES
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|int
name|PROFILE_CYCLES
init|=
literal|1000000
decl_stmt|;
specifier|public
name|JMSTransformationSpeedComparisonTest
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
specifier|private
name|InboundTransformer
name|getInboundTransformer
parameter_list|()
block|{
switch|switch
condition|(
name|transformer
condition|)
block|{
case|case
literal|"raw"
case|:
return|return
operator|new
name|AMQPRawInboundTransformer
argument_list|()
return|;
case|case
literal|"native"
case|:
return|return
operator|new
name|AMQPNativeInboundTransformer
argument_list|()
return|;
default|default:
return|return
operator|new
name|JMSMappingInboundTransformer
argument_list|()
return|;
block|}
block|}
specifier|private
name|OutboundTransformer
name|getOutboundTransformer
parameter_list|()
block|{
switch|switch
condition|(
name|transformer
condition|)
block|{
case|case
literal|"raw"
case|:
case|case
literal|"native"
case|:
return|return
operator|new
name|AMQPNativeOutboundTransformer
argument_list|()
return|;
default|default:
return|return
operator|new
name|JMSMappingOutboundTransformer
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBodyOnlyMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|Proton
operator|.
name|message
argument_list|()
decl_stmt|;
name|message
operator|.
name|setBody
argument_list|(
operator|new
name|AmqpValue
argument_list|(
literal|"String payload for AMQP message conversion performance testing."
argument_list|)
argument_list|)
expr_stmt|;
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
name|OutboundTransformer
name|outboundTransformer
init|=
name|getOutboundTransformer
argument_list|()
decl_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageWithNoPropertiesOrAnnotations
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|Proton
operator|.
name|message
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAddress
argument_list|(
literal|"queue://test-queue"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDeliveryCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCreationTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
operator|new
name|AmqpValue
argument_list|(
literal|"String payload for AMQP message conversion performance testing."
argument_list|)
argument_list|)
expr_stmt|;
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
name|OutboundTransformer
name|outboundTransformer
init|=
name|getOutboundTransformer
argument_list|()
decl_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTypicalQpidJMSMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|createTypicalQpidJMSMessage
argument_list|()
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
name|OutboundTransformer
name|outboundTransformer
init|=
name|getOutboundTransformer
argument_list|()
decl_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testComplexQpidJMSMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|createComplexQpidJMSMessage
argument_list|()
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
name|OutboundTransformer
name|outboundTransformer
init|=
name|getOutboundTransformer
argument_list|()
decl_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|ActiveMQMessage
name|intermediate
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|intermediate
operator|.
name|onSend
argument_list|()
expr_stmt|;
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|intermediate
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTypicalQpidJMSMessageInBoundOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|createTypicalQpidJMSMessage
argument_list|()
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTypicalQpidJMSMessageOutBoundOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|EncodedMessage
name|encoded
init|=
name|encode
argument_list|(
name|createTypicalQpidJMSMessage
argument_list|()
argument_list|)
decl_stmt|;
name|InboundTransformer
name|inboundTransformer
init|=
name|getInboundTransformer
argument_list|()
decl_stmt|;
name|OutboundTransformer
name|outboundTransformer
init|=
name|getOutboundTransformer
argument_list|()
decl_stmt|;
name|ActiveMQMessage
name|outbound
init|=
name|inboundTransformer
operator|.
name|transform
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|outbound
operator|.
name|onSend
argument_list|()
expr_stmt|;
comment|// Warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WARM_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|outbound
argument_list|)
expr_stmt|;
block|}
name|long
name|totalDuration
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PROFILE_CYCLES
condition|;
operator|++
name|i
control|)
block|{
name|outboundTransformer
operator|.
name|transform
argument_list|(
name|outbound
argument_list|)
expr_stmt|;
block|}
name|totalDuration
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[{}] Total time for {} cycles of transforms = {} ms  -> [{}]"
argument_list|,
name|transformer
argument_list|,
name|PROFILE_CYCLES
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|totalDuration
argument_list|)
argument_list|,
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Message
name|createTypicalQpidJMSMessage
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|applicationProperties
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
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|messageAnnotations
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-1"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-2"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|messageAnnotations
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-jms-msg-type"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|messageAnnotations
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-jms-dest"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|Proton
operator|.
name|message
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAddress
argument_list|(
literal|"queue://test-queue"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDeliveryCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setApplicationProperties
argument_list|(
operator|new
name|ApplicationProperties
argument_list|(
name|applicationProperties
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageAnnotations
argument_list|(
operator|new
name|MessageAnnotations
argument_list|(
name|messageAnnotations
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCreationTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
operator|new
name|AmqpValue
argument_list|(
literal|"String payload for AMQP message conversion performance testing."
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|private
name|Message
name|createComplexQpidJMSMessage
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|applicationProperties
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
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|messageAnnotations
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-1"
argument_list|,
literal|"string-1"
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-2"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-4"
argument_list|,
literal|"string-2"
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-5"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-6"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-7"
argument_list|,
literal|"string-3"
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-8"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|applicationProperties
operator|.
name|put
argument_list|(
literal|"property-9"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|messageAnnotations
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-jms-msg-type"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|messageAnnotations
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-jms-dest"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|Proton
operator|.
name|message
argument_list|()
decl_stmt|;
comment|// Header Values
name|message
operator|.
name|setPriority
argument_list|(
operator|(
name|short
operator|)
literal|9
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDurable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDeliveryCount
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTtl
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// Properties
name|message
operator|.
name|setMessageId
argument_list|(
literal|"ID:SomeQualifier:0:0:1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupId
argument_list|(
literal|"Group-ID-1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupSequence
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|message
operator|.
name|setAddress
argument_list|(
literal|"queue://test-queue"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setReplyTo
argument_list|(
literal|"queue://reply-queue"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCreationTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCorrelationId
argument_list|(
literal|"ID:SomeQualifier:0:7:9"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setUserId
argument_list|(
literal|"username"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
comment|// Application Properties / Message Annotations / Body
name|message
operator|.
name|setApplicationProperties
argument_list|(
operator|new
name|ApplicationProperties
argument_list|(
name|applicationProperties
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageAnnotations
argument_list|(
operator|new
name|MessageAnnotations
argument_list|(
name|messageAnnotations
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
operator|new
name|AmqpValue
argument_list|(
literal|"String payload for AMQP message conversion performance testing."
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|private
name|EncodedMessage
name|encode
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|ProtonJMessage
name|amqp
init|=
operator|(
name|ProtonJMessage
operator|)
name|message
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|4
index|]
argument_list|)
decl_stmt|;
specifier|final
name|DroppingWritableBuffer
name|overflow
init|=
operator|new
name|DroppingWritableBuffer
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|amqp
operator|.
name|encode
argument_list|(
operator|new
name|CompositeWritableBuffer
argument_list|(
operator|new
name|WritableBuffer
operator|.
name|ByteBufferWrapper
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|overflow
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|overflow
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|4
operator|+
name|overflow
operator|.
name|position
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|c
operator|=
name|amqp
operator|.
name|encode
argument_list|(
operator|new
name|WritableBuffer
operator|.
name|ByteBufferWrapper
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EncodedMessage
argument_list|(
literal|1
argument_list|,
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
return|;
block|}
block|}
end_class

end_unit

