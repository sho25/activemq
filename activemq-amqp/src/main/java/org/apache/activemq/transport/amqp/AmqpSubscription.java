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
name|command
operator|.
name|*
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
name|util
operator|.
name|zip
operator|.
name|DataFormatException
import|;
end_import

begin_comment
comment|/**  * Keeps track of the AMQP client subscription so that acking is correctly done.  */
end_comment

begin_class
class|class
name|AmqpSubscription
block|{
comment|//    private final AmqpProtocolConverter protocolConverter;
comment|//
comment|//    private final ConsumerInfo consumerInfo;
comment|//    private ActiveMQDestination destination;
comment|//    private final QoS qos;
comment|//
comment|//    public AmqpSubscription(AmqpProtocolConverter protocolConverter, QoS qos, ConsumerInfo consumerInfo) {
comment|//        this.protocolConverter = protocolConverter;
comment|//        this.consumerInfo = consumerInfo;
comment|//        this.qos = qos;
comment|//    }
comment|//
comment|//    MessageAck createMessageAck(MessageDispatch md) {
comment|//        return new MessageAck(md, MessageAck.STANDARD_ACK_TYPE, 1);
comment|//    }
comment|//
comment|//    PUBLISH createPublish(ActiveMQMessage message) throws DataFormatException, IOException, JMSException {
comment|//        PUBLISH publish = protocolConverter.convertMessage(message);
comment|//        if (publish.qos().ordinal()> this.qos.ordinal()) {
comment|//            publish.qos(this.qos);
comment|//        }
comment|//        return publish;
comment|//    }
comment|//
comment|//    public boolean expectAck() {
comment|//        return qos != QoS.AT_MOST_ONCE;
comment|//    }
comment|//
comment|//    public void setDestination(ActiveMQDestination destination) {
comment|//        this.destination = destination;
comment|//    }
comment|//
comment|//    public ActiveMQDestination getDestination() {
comment|//        return destination;
comment|//    }
comment|//
comment|//    public ConsumerInfo getConsumerInfo() {
comment|//        return consumerInfo;
comment|//    }
block|}
end_class

end_unit

