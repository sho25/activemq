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
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Service
import|;
end_import

begin_comment
comment|/**  * A Job Scheduler Store interface use to manage delay processing of Messaging  * related jobs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JobSchedulerStore
extends|extends
name|Service
block|{
comment|/**      * Gets the location where the Job Scheduler will write the persistent data used      * to preserve and recover scheduled Jobs.      *      * If the scheduler implementation does not utilize a file system based store this      * method returns null.      *      * @return the directory where persistent store data is written.      */
name|File
name|getDirectory
parameter_list|()
function_decl|;
comment|/**      * Sets the directory where persistent store data will be written.  This method      * must be called before the scheduler store is started to have any effect.      *      * @param directory      *      The directory where the job scheduler store is to be located.      */
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
function_decl|;
comment|/**      * The size of the current store on disk if the store utilizes a disk based store      * mechanism.      *      * @return the current store size on disk.      */
name|long
name|size
parameter_list|()
function_decl|;
comment|/**      * Returns the JobScheduler instance identified by the given name.      *      * @param name      *        the name of the JobScheduler instance to lookup.      *      * @return the named JobScheduler or null if none exists with the given name.      *      * @throws Exception if an error occurs while loading the named scheduler.      */
name|JobScheduler
name|getJobScheduler
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes the named JobScheduler if it exists, purging all scheduled messages      * assigned to it.      *      * @param name      *        the name of the scheduler instance to remove.      *      * @return true if there was a scheduler with the given name to remove.      *      * @throws Exception if an error occurs while removing the scheduler.      */
name|boolean
name|removeJobScheduler
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

