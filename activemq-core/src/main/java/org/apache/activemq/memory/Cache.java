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
name|memory
package|;
end_package

begin_comment
comment|/**  * Defines the interface used to cache messages.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cache
block|{
comment|/**      * Gets an object that was previously<code>put</code> into this object.      *       * @param msgid      * @return null if the object was not previously put or if the object has      *         expired out of the cache.      */
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * Puts an object into the cache.      *       * @param messageID      * @param message      */
specifier|public
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Removes an object from the cache.      *       * @param messageID      * @return the object associated with the key if it was still in the cache.      */
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * Lets a cache know it will not be used any further and that it can release      * acquired resources      */
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * How big is the cache right now?      *       * @return      */
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

