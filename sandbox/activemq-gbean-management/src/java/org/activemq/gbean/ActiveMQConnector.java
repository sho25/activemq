begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Protique Ltd  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|gbean
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|management
operator|.
name|geronimo
operator|.
name|JMSConnector
import|;
end_import

begin_comment
comment|/**  * The GBean interface for the ActiveMQ network connector GBean  *  * @version $Revision: 1.0$  */
end_comment

begin_interface
specifier|public
interface|interface
name|ActiveMQConnector
extends|extends
name|JMSConnector
block|{
specifier|public
specifier|final
specifier|static
name|String
name|CONNECTOR_J2EE_TYPE
init|=
literal|"JMSConnector"
decl_stmt|;
comment|// Additional stuff you can add to an ActiveMQ connector URI
specifier|public
name|String
name|getPath
parameter_list|()
function_decl|;
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
specifier|public
name|String
name|getQuery
parameter_list|()
function_decl|;
specifier|public
name|void
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

