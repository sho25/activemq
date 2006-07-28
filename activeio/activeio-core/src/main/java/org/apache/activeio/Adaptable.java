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
name|activeio
package|;
end_package

begin_comment
comment|/**  * Provides an Adaptable interface inspired by eclipse's IAdaptable class.  Highly used in ActiveIO since Channel and Packet  * implementations may be layered and application code may want request the higher level layers/abstractions to adapt to give access  * to the lower layer implementation details.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Adaptable
block|{
comment|/**      *  @Return object that is an instance of requested type and is associated this this object.  May return null if no       *  object of that type is associated.      */
name|Object
name|getAdapter
parameter_list|(
name|Class
name|target
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

