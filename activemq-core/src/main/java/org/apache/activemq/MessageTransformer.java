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
package|;
end_package

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
name|Session
import|;
end_import

begin_comment
comment|/**  * A plugin strategy for transforming a message before it is sent by the JMS client or before it is  * dispatched to the JMS consumer  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageTransformer
block|{
comment|/**      * Transforms the given message inside the producer before it is sent to the JMS bus.      */
name|Message
name|producerTransform
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Transforms the given message inside the consumer before being dispatched to the client code      */
name|Message
name|consumerTransform
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit

