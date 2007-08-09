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
name|camel
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
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
name|ActiveMQSession
import|;
end_import

begin_comment
comment|/**  * A JMS {@link Queue} object which refers to a Camel endpoint  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|CamelQueue
extends|extends
name|CamelDestination
implements|implements
name|Queue
block|{
specifier|public
name|CamelQueue
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getQueueName
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getUri
argument_list|()
return|;
block|}
specifier|public
name|QueueSender
name|createSender
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|CamelQueueSender
argument_list|(
name|this
argument_list|,
name|resolveEndpoint
argument_list|(
name|session
argument_list|)
argument_list|,
name|session
argument_list|)
return|;
block|}
specifier|public
name|QueueReceiver
name|createReceiver
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|messageSelector
parameter_list|)
block|{
return|return
operator|new
name|CamelQueueReceiver
argument_list|(
name|this
argument_list|,
name|resolveEndpoint
argument_list|(
name|session
argument_list|)
argument_list|,
name|session
argument_list|,
name|messageSelector
argument_list|)
return|;
block|}
block|}
end_class

end_unit

