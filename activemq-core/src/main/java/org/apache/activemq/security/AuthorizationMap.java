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
name|security
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthorizationMap
block|{
comment|/**      * Returns the set of all ACLs capable of administering temp destination      */
name|Set
name|getTempDestinationAdminACLs
parameter_list|()
function_decl|;
comment|/**      * Returns the set of all ACLs capable of reading from temp destination      */
name|Set
name|getTempDestinationReadACLs
parameter_list|()
function_decl|;
comment|/**      * Returns the set of all ACLs capable of writing to temp destination      */
name|Set
name|getTempDestinationWriteACLs
parameter_list|()
function_decl|;
comment|/**      * Returns the set of all ACLs capable of administering the given destination      */
name|Set
name|getAdminACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * Returns the set of all ACLs capable of reading (consuming from) the given destination      */
name|Set
name|getReadACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * Returns the set of all ACLs capable of writing to the given destination      */
name|Set
name|getWriteACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

