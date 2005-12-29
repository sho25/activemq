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
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicTransactionTest
extends|extends
name|JmsTransactionTestSupport
block|{
comment|/**      * @see org.apache.activemq.JmsTransactionTestSupport#getJmsResourceProvider()      */
specifier|protected
name|JmsResourceProvider
name|getJmsResourceProvider
parameter_list|()
block|{
name|JmsResourceProvider
name|p
init|=
operator|new
name|JmsResourceProvider
argument_list|()
decl_stmt|;
name|p
operator|.
name|setTopic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|p
operator|.
name|setDurableName
argument_list|(
literal|"testsub"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setClientID
argument_list|(
literal|"testclient"
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

