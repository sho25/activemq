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
name|ra
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
name|resource
operator|.
name|spi
operator|.
name|BootstrapContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ResourceAdapter
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
name|ActiveMQConnection
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
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * Knows how to connect to one ActiveMQ server. It can then activate endpoints  * and deliver messages to those end points using the connection configure in  * the resource adapter.<p/>Must override equals and hashCode (JCA spec 16.4)  *  * @org.apache.xbean.XBean element="resourceAdapter" rootElement="true"  * description="The JCA Resource Adaptor for ActiveMQ"  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageResourceAdapter
extends|extends
name|ResourceAdapter
block|{
comment|/**      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/** 	 * @param activationSpec      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|MessageActivationSpec
name|activationSpec
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/** 	 * @return bootstrap context      */
specifier|public
name|BootstrapContext
name|getBootstrapContext
parameter_list|()
function_decl|;
comment|/**      */
specifier|public
name|String
name|getBrokerXmlConfig
parameter_list|()
function_decl|;
comment|/**      * @return Returns the info.      */
specifier|public
name|ActiveMQConnectionRequestInfo
name|getInfo
parameter_list|()
function_decl|;
comment|/**      */
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

