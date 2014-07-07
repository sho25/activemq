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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_comment
comment|/**  * Job event listener interface. Provides event points for Job related events  * such as job ready events.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JobListener
block|{
comment|/**      * A Job that has been scheduled is now ready to be fired.  The Job is passed      * in its raw byte form and must be un-marshaled before being delivered.      *      * @param jobId      *        The unique Job Id of the Job that is ready to fire.      * @param job      *        The job that is now ready, delivered in byte form.      */
specifier|public
name|void
name|scheduledJob
parameter_list|(
name|String
name|id
parameter_list|,
name|ByteSequence
name|job
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

