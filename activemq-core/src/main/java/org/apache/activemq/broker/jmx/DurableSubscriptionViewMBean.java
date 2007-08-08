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
name|broker
operator|.
name|jmx
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
name|broker
operator|.
name|ConnectionContext
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
name|command
operator|.
name|RemoveSubscriptionInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|DurableSubscriptionViewMBean
extends|extends
name|SubscriptionViewMBean
block|{
comment|/**      * @return name of the durable subscription name      */
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
function_decl|;
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
specifier|public
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Destroys the durable subscription so that messages will no longer be stored for this subscription      */
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

