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
name|network
operator|.
name|jms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jndi
operator|.
name|JndiTemplate
import|;
end_import

begin_comment
comment|/**  * A Bridge to other JMS Queue providers  *  * @org.apache.xbean.XBean  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueConnector
extends|extends
name|SimpleJmsQueueConnector
block|{
specifier|public
name|void
name|setJndiLocalTemplate
parameter_list|(
name|JndiTemplate
name|template
parameter_list|)
block|{
name|super
operator|.
name|setJndiLocalTemplate
argument_list|(
operator|new
name|JndiTemplateLookupFactory
argument_list|(
name|template
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setJndiOutboundTemplate
parameter_list|(
name|JndiTemplate
name|template
parameter_list|)
block|{
name|super
operator|.
name|setJndiOutboundTemplate
argument_list|(
operator|new
name|JndiTemplateLookupFactory
argument_list|(
name|template
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

