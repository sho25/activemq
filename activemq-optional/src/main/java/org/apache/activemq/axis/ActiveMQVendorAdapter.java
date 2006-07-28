begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|axis
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
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|components
operator|.
name|jms
operator|.
name|BeanVendorAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|transport
operator|.
name|jms
operator|.
name|JMSURLHelper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
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

begin_comment
comment|/**  * An adapter for using ActiveMQ inside<a href="http://ws.apache.org/axis/">Apache Axis</a>  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQVendorAdapter
extends|extends
name|BeanVendorAdapter
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|QCF_CLASS
init|=
name|ActiveMQConnectionFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TCF_CLASS
init|=
name|QCF_CLASS
decl_stmt|;
comment|/**      * The URL to connect to the broker      */
specifier|public
specifier|final
specifier|static
name|String
name|BROKER_URL
init|=
literal|"brokerURL"
decl_stmt|;
comment|/**      * Specifies the default user name      */
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_USERNAME
init|=
literal|"defaultUser"
decl_stmt|;
comment|/**      * Specifies the default password      */
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_PASSWORD
init|=
literal|"defaultPassword"
decl_stmt|;
specifier|public
name|QueueConnectionFactory
name|getQueueConnectionFactory
parameter_list|(
name|HashMap
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|properties
operator|=
operator|(
name|HashMap
operator|)
name|properties
operator|.
name|clone
argument_list|()
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|CONNECTION_FACTORY_CLASS
argument_list|,
name|QCF_CLASS
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getQueueConnectionFactory
argument_list|(
name|properties
argument_list|)
return|;
block|}
specifier|public
name|TopicConnectionFactory
name|getTopicConnectionFactory
parameter_list|(
name|HashMap
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|properties
operator|=
operator|(
name|HashMap
operator|)
name|properties
operator|.
name|clone
argument_list|()
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|CONNECTION_FACTORY_CLASS
argument_list|,
name|TCF_CLASS
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getTopicConnectionFactory
argument_list|(
name|properties
argument_list|)
return|;
block|}
specifier|public
name|void
name|addVendorConnectionFactoryProperties
parameter_list|(
name|JMSURLHelper
name|jmsUrl
parameter_list|,
name|HashMap
name|properties
parameter_list|)
block|{
if|if
condition|(
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|BROKER_URL
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|BROKER_URL
argument_list|,
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|BROKER_URL
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|DEFAULT_USERNAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|DEFAULT_USERNAME
argument_list|,
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|DEFAULT_USERNAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|DEFAULT_PASSWORD
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|DEFAULT_PASSWORD
argument_list|,
name|jmsUrl
operator|.
name|getPropertyValue
argument_list|(
name|DEFAULT_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isMatchingConnectionFactory
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|,
name|JMSURLHelper
name|jmsURL
parameter_list|,
name|HashMap
name|properties
parameter_list|)
block|{
name|String
name|brokerURL
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|connectionFactory
operator|instanceof
name|ActiveMQConnectionFactory
condition|)
block|{
name|ActiveMQConnectionFactory
name|amqConnectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|connectionFactory
decl_stmt|;
comment|// get existing queue connection factory properties
name|brokerURL
operator|=
name|amqConnectionFactory
operator|.
name|getBrokerURL
argument_list|()
expr_stmt|;
block|}
comment|// compare broker url
name|String
name|propertyBrokerURL
init|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
name|BROKER_URL
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|brokerURL
operator|.
name|equals
argument_list|(
name|propertyBrokerURL
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

