begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
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
name|region
operator|.
name|group
package|;
end_package

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageGroupHashBucketTest
extends|extends
name|MessageGroupMapTest
block|{
specifier|protected
name|MessageGroupMap
name|createMessageGroupMap
parameter_list|()
block|{
return|return
operator|new
name|MessageGroupHashBucket
argument_list|(
literal|1024
argument_list|)
return|;
block|}
block|}
end_class

end_unit

