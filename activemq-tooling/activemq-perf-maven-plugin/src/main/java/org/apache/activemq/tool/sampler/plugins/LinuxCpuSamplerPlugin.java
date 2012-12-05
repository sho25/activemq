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
name|tool
operator|.
name|sampler
operator|.
name|plugins
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
specifier|public
class|class
name|LinuxCpuSamplerPlugin
implements|implements
name|CpuSamplerPlugin
implements|,
name|Runnable
block|{
specifier|private
name|Process
name|vmstatProcess
decl_stmt|;
specifier|private
name|String
name|vmstat
decl_stmt|;
specifier|private
name|String
name|result
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|LinuxCpuSamplerPlugin
parameter_list|(
name|long
name|intervalInMs
parameter_list|)
block|{
name|vmstat
operator|=
literal|"vmstat -n "
operator|+
call|(
name|int
call|)
argument_list|(
name|intervalInMs
operator|/
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|stop
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|vmstatProcess
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|vmstatProcess
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|vmstat
argument_list|)
expr_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|vmstatProcess
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|// throw away the first line
name|String
name|header
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|String
name|data
decl_stmt|;
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|data
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|String
name|csvData
init|=
name|convertToCSV
argument_list|(
name|header
argument_list|,
name|data
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|result
operator|=
name|csvData
expr_stmt|;
block|}
block|}
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|vmstatProcess
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getCpuUtilizationStats
parameter_list|()
block|{
name|String
name|data
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|data
operator|=
name|result
expr_stmt|;
name|result
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
specifier|public
name|String
name|getVmstat
parameter_list|()
block|{
return|return
name|vmstat
return|;
block|}
specifier|public
name|void
name|setVmstat
parameter_list|(
name|String
name|vmstat
parameter_list|)
block|{
name|this
operator|.
name|vmstat
operator|=
name|vmstat
expr_stmt|;
block|}
specifier|protected
name|String
name|convertToCSV
parameter_list|(
name|String
name|header
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|StringTokenizer
name|headerTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|header
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
name|StringTokenizer
name|dataTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|data
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
name|String
name|csv
init|=
literal|""
decl_stmt|;
while|while
condition|(
name|headerTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|csv
operator|+=
name|headerTokens
operator|.
name|nextToken
argument_list|()
operator|+
literal|"="
operator|+
name|dataTokens
operator|.
name|nextToken
argument_list|()
operator|+
literal|","
expr_stmt|;
block|}
return|return
name|csv
return|;
block|}
block|}
end_class

end_unit
