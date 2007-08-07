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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQSession
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
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_comment
comment|/**  * A JMS {@link javax.jms.Topic} object which refers to a Camel endpoint  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|CamelTopic
extends|extends
name|CamelDestination
implements|implements
name|Topic
block|{
specifier|public
name|CamelTopic
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
name|getTopicName
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
name|TopicPublisher
name|createPublisher
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|CamelTopicPublisher
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
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
block|{
return|return
operator|new
name|CamelTopicSubscriber
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
name|name
argument_list|,
name|messageSelector
argument_list|,
name|noLocal
argument_list|)
return|;
block|}
block|}
end_class

end_unit

