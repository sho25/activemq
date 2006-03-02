begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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

begin_comment
comment|/**  * A simple marshaller for Destinations  *  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|DestinationMarshaller
block|{
comment|/**      * Builds a destination from a destinationName      * @param destinationName       *      * @return the destination to send messages to all members of the cluster      */
specifier|public
name|Destination
name|getDestination
parameter_list|(
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Gets a destination's physical name      * @param destination      * @return the destination's physical name      */
specifier|public
name|String
name|getDestinationName
parameter_list|(
name|Destination
name|destination
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

