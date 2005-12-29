begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DeliveryMode
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
name|test
operator|.
name|JmsResourceProvider
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsDurableTopicTransactionTest
extends|extends
name|JmsTopicTransactionTest
block|{
comment|/**      * @see JmsTransactionTestSupport#getJmsResourceProvider()      */
specifier|protected
name|JmsResourceProvider
name|getJmsResourceProvider
parameter_list|()
block|{
name|JmsResourceProvider
name|provider
init|=
operator|new
name|JmsResourceProvider
argument_list|()
decl_stmt|;
name|provider
operator|.
name|setTopic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setClientID
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setDurableName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|provider
return|;
block|}
block|}
end_class

end_unit

