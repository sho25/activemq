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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|hash
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
name|kaha
operator|.
name|IndexMBean
import|;
end_import

begin_comment
comment|/**  * MBean for HashIndex  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|HashIndexMBean
extends|extends
name|IndexMBean
block|{
comment|/**      * @return the keySize      */
specifier|public
name|int
name|getKeySize
parameter_list|()
function_decl|;
comment|/**      * @param keySize the keySize to set      */
specifier|public
name|void
name|setKeySize
parameter_list|(
name|int
name|keySize
parameter_list|)
function_decl|;
comment|/**      * @return the page size      */
specifier|public
name|int
name|getPageSize
parameter_list|()
function_decl|;
comment|/**      * @return number of bins      */
specifier|public
name|int
name|getNumberOfBins
parameter_list|()
function_decl|;
comment|/**      * @return the enablePageCaching      */
specifier|public
name|boolean
name|isEnablePageCaching
parameter_list|()
function_decl|;
comment|/**      * @return the pageCacheSize      */
specifier|public
name|int
name|getPageCacheSize
parameter_list|()
function_decl|;
comment|/**      * @return size      */
specifier|public
name|int
name|getSize
parameter_list|()
function_decl|;
comment|/**      * @return the number of active bins      */
specifier|public
name|int
name|getActiveBins
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

