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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A lockable broker resource. Uses {@link Locker} to guarantee that only single instance is running  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Lockable
block|{
comment|/**      * Turn locking on/off on the resource      *      * @param useLock      */
specifier|public
name|void
name|setUseLock
parameter_list|(
name|boolean
name|useLock
parameter_list|)
function_decl|;
comment|/**      * Create a default locker      *      * @return default locker      * @throws IOException      */
specifier|public
name|Locker
name|createDefaultLocker
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Set locker to be used      *      * @param locker      * @throws IOException      */
specifier|public
name|void
name|setLocker
parameter_list|(
name|Locker
name|locker
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Period (in milliseconds) on which {@link org.apache.activemq.broker.Locker#keepAlive()} should be checked      *      * @param lockKeepAlivePeriod      */
specifier|public
name|void
name|setLockKeepAlivePeriod
parameter_list|(
name|long
name|lockKeepAlivePeriod
parameter_list|)
function_decl|;
name|long
name|getLockKeepAlivePeriod
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

