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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

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
comment|/**  * Represents a lock service to ensure that a broker is the only master  */
end_comment

begin_interface
specifier|public
interface|interface
name|Locker
extends|extends
name|Service
block|{
comment|/**      * Used by a timer to keep alive the lock.      * If the method returns false the broker should be terminated      * if an exception is thrown, the lock state cannot be determined      */
name|boolean
name|keepAlive
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * set the delay interval in milliseconds between lock acquire attempts      *      * @param lockAcquireSleepInterval the sleep interval in miliseconds      */
name|void
name|setLockAcquireSleepInterval
parameter_list|(
name|long
name|lockAcquireSleepInterval
parameter_list|)
function_decl|;
comment|/**      * Set the name of the lock to use.      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Specify whether to fail immediately if the lock is already held.  When set, the CustomLock must throw an      * IOException immediately upon detecting the lock is already held.      *      * @param failIfLocked: true => fail immediately if the lock is held; false => block until the lock can be obtained      *                      (default).      */
specifier|public
name|void
name|setFailIfLocked
parameter_list|(
name|boolean
name|failIfLocked
parameter_list|)
function_decl|;
comment|/**      * Optionally configure the locker with the persistence adapter currently used      * You can use persistence adapter configuration details like, data directory      * datasource, etc. to be used by the locker      *      * @param persistenceAdapter      * @throws IOException      */
specifier|public
name|void
name|configure
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

