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
name|usage
package|;
end_package

begin_comment
comment|/**  Identify if a limit has been reached  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|UsageCapacity
block|{
comment|/**      * Has the limit been reached ?      *       * @param size      * @return true if it has      */
name|boolean
name|isLimit
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
comment|/**      * @return the limit      */
name|long
name|getLimit
parameter_list|()
function_decl|;
comment|/**      * @param limit the limit to set      */
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

