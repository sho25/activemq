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

begin_comment
comment|/**  * A useful base class for message transformers.  *  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageTransformerSupport
implements|implements
name|MessageTransformer
block|{
comment|/**      * Copies the standard JMS and user defined properties from the givem message to the specified message      *      * @param fromMessage the message to take the properties from      * @param toMesage the message to add the properties to      * @throws JMSException      */
specifier|protected
name|void
name|copyProperties
parameter_list|(
name|Message
name|fromMessage
parameter_list|,
name|Message
name|toMesage
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQMessageTransformation
operator|.
name|copyProperties
argument_list|(
name|fromMessage
argument_list|,
name|toMesage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

