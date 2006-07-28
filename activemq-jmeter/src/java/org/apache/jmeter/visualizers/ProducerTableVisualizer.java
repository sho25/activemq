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
name|jmeter
operator|.
name|visualizers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|Clearable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|SampleResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|util
operator|.
name|JMeterUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|visualizers
operator|.
name|gui
operator|.
name|AbstractVisualizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|gui
operator|.
name|ObjectTableModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|gui
operator|.
name|layout
operator|.
name|VerticalLayout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|logging
operator|.
name|LoggingManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|reflect
operator|.
name|Functor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|sampler
operator|.
name|Producer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextField
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPanel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|BorderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JLabel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|border
operator|.
name|Border
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|border
operator|.
name|EmptyBorder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|BorderLayout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Color
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|FlowLayout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_comment
comment|/**  * A tableVisualizer that can display Producer Output  */
end_comment

begin_class
specifier|public
class|class
name|ProducerTableVisualizer
extends|extends
name|AbstractVisualizer
implements|implements
name|Clearable
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggingManager
operator|.
name|getLoggerForClass
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|COLUMNS
init|=
operator|new
name|String
index|[]
block|{
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_num"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_processed"
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SECOND
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|INSECONDS
init|=
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MINUTE
init|=
name|SECOND
operator|*
name|INSECONDS
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DecimalFormat
name|dFormat
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"#,###,###"
argument_list|)
decl_stmt|;
specifier|private
name|ObjectTableModel
name|model
init|=
literal|null
decl_stmt|;
specifier|private
name|JTable
name|table
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextField
name|averageField
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextField
name|totalMsgsField
init|=
literal|null
decl_stmt|;
specifier|private
name|JScrollPane
name|tableScrollPanel
init|=
literal|null
decl_stmt|;
specifier|private
name|double
name|average
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|total
init|=
literal|0
decl_stmt|;
comment|/**      * Constructor for the TableVisualizer object.      */
specifier|public
name|ProducerTableVisualizer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|model
operator|=
operator|new
name|ObjectTableModel
argument_list|(
name|COLUMNS
argument_list|,
operator|new
name|Functor
index|[]
block|{
operator|new
name|Functor
argument_list|(
literal|"getCount"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getData"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getProcessed"
argument_list|)
block|}
argument_list|,
operator|new
name|Functor
index|[]
block|{
literal|null
block|,
literal|null
block|,
literal|null
block|}
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Long
operator|.
name|class
block|,
name|Long
operator|.
name|class
block|,
name|Long
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Label key to get from label resource      */
specifier|public
name|String
name|getLabelResource
parameter_list|()
block|{
return|return
literal|"view_prod_results_in_table"
return|;
block|}
comment|/**      * Sets the average Field and total Messages sent, that would be displayed.      */
specifier|protected
specifier|synchronized
name|void
name|updateTextFields
parameter_list|()
block|{
name|averageField
operator|.
name|setText
argument_list|(
name|dFormat
operator|.
name|format
argument_list|(
name|average
argument_list|)
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setText
argument_list|(
name|dFormat
operator|.
name|format
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param res SampleResult from the JMeter Sampler      */
specifier|public
name|void
name|add
parameter_list|(
name|SampleResult
name|res
parameter_list|)
block|{
name|Thread
name|timer
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|timerLoop
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|timer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * clear/resets the field.      */
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Clear called"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Debug"
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|clearData
argument_list|()
expr_stmt|;
name|averageField
operator|.
name|setText
argument_list|(
literal|"0000"
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setText
argument_list|(
literal|"0000"
argument_list|)
expr_stmt|;
name|repaint
argument_list|()
expr_stmt|;
block|}
comment|/**      * Initialize the User Interface      */
specifier|private
name|void
name|init
parameter_list|()
block|{
name|this
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
comment|// Main Panel
name|JPanel
name|mainPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|Border
name|margin
init|=
operator|new
name|EmptyBorder
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|mainPanel
operator|.
name|setBorder
argument_list|(
name|margin
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|setLayout
argument_list|(
operator|new
name|VerticalLayout
argument_list|(
literal|5
argument_list|,
name|VerticalLayout
operator|.
name|LEFT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Name
name|mainPanel
operator|.
name|add
argument_list|(
name|makeTitlePanel
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set up the table itself
name|table
operator|=
operator|new
name|JTable
argument_list|(
name|model
argument_list|)
expr_stmt|;
comment|// table.getTableHeader().setReorderingAllowed(false);
name|tableScrollPanel
operator|=
operator|new
name|JScrollPane
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|tableScrollPanel
operator|.
name|setViewportBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set up footer of table which displays numerics of the graphs
name|JPanel
name|averagePanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|JLabel
name|averageLabel
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"graph_results_average"
argument_list|)
argument_list|)
decl_stmt|;
name|averageLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|averageField
operator|=
operator|new
name|JTextField
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|averageField
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|averageField
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|averageField
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|averageField
operator|.
name|setBackground
argument_list|(
name|getBackground
argument_list|()
argument_list|)
expr_stmt|;
name|averagePanel
operator|.
name|add
argument_list|(
name|averageLabel
argument_list|)
expr_stmt|;
name|averagePanel
operator|.
name|add
argument_list|(
name|averageField
argument_list|)
expr_stmt|;
name|JPanel
name|totalMsgsPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|JLabel
name|totalMsgsLabel
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"graph_results_total_msgs"
argument_list|)
argument_list|)
decl_stmt|;
name|totalMsgsLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|=
operator|new
name|JTextField
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|totalMsgsField
operator|.
name|setBackground
argument_list|(
name|getBackground
argument_list|()
argument_list|)
expr_stmt|;
name|totalMsgsPanel
operator|.
name|add
argument_list|(
name|totalMsgsLabel
argument_list|)
expr_stmt|;
name|totalMsgsPanel
operator|.
name|add
argument_list|(
name|totalMsgsField
argument_list|)
expr_stmt|;
name|JPanel
name|tableInfoPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|tableInfoPanel
operator|.
name|setLayout
argument_list|(
operator|new
name|FlowLayout
argument_list|()
argument_list|)
expr_stmt|;
name|tableInfoPanel
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|tableInfoPanel
operator|.
name|add
argument_list|(
name|averagePanel
argument_list|)
expr_stmt|;
name|tableInfoPanel
operator|.
name|add
argument_list|(
name|totalMsgsPanel
argument_list|)
expr_stmt|;
comment|// Set up the table with footer
name|JPanel
name|tablePanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|tablePanel
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
name|tablePanel
operator|.
name|add
argument_list|(
name|tableScrollPanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|tablePanel
operator|.
name|add
argument_list|(
name|tableInfoPanel
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
comment|// Add the main panel and the graph
name|this
operator|.
name|add
argument_list|(
name|mainPanel
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|this
operator|.
name|add
argument_list|(
name|tablePanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
block|}
comment|/**      * gets the number of processed messages.      */
specifier|protected
name|void
name|timerLoop
parameter_list|()
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|difInTime
init|=
literal|0
decl_stmt|;
name|long
name|currTime
init|=
literal|0
decl_stmt|;
name|long
name|difInMins
init|=
literal|0
decl_stmt|;
name|long
name|difInSec
init|=
literal|0
decl_stmt|;
name|long
name|ramp_upInSec
init|=
literal|0
decl_stmt|;
name|long
name|timeInSec
init|=
literal|0
decl_stmt|;
name|int
name|msgCounter
init|=
literal|0
decl_stmt|;
name|long
name|duration
init|=
name|Producer
operator|.
name|duration
decl_stmt|;
name|long
name|ramp_up
init|=
name|Producer
operator|.
name|ramp_up
decl_stmt|;
while|while
condition|(
name|difInMins
operator|<
name|duration
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
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
name|currTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|difInTime
operator|=
name|currTime
operator|-
name|startTime
expr_stmt|;
name|difInMins
operator|=
name|difInTime
operator|/
name|MINUTE
expr_stmt|;
name|timeInSec
operator|=
name|difInTime
operator|/
name|SECOND
expr_stmt|;
name|difInSec
operator|=
operator|(
operator|(
name|duration
operator|*
name|INSECONDS
operator|)
operator|-
name|timeInSec
operator|)
expr_stmt|;
name|ramp_upInSec
operator|=
name|ramp_up
operator|*
name|INSECONDS
expr_stmt|;
name|long
name|processed
init|=
name|Producer
operator|.
name|resetCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|processed
operator|>
literal|0
operator|&&
operator|(
name|difInMins
operator|>=
name|ramp_up
operator|)
operator|&&
operator|(
operator|(
name|duration
operator|-
name|difInMins
operator|)
operator|>=
name|ramp_up
operator|)
operator|&&
operator|(
name|difInSec
operator|>=
name|ramp_upInSec
operator|)
condition|)
block|{
if|if
condition|(
name|timeInSec
operator|>
name|ramp_upInSec
condition|)
block|{
name|total
operator|+=
name|processed
expr_stmt|;
name|average
operator|=
name|total
operator|/
operator|(
name|timeInSec
operator|-
name|ramp_upInSec
operator|)
expr_stmt|;
block|}
comment|// Update the footer data.
name|updateTextFields
argument_list|()
expr_stmt|;
block|}
comment|// Add data to table row.
name|MessageSample
name|newS
init|=
operator|new
name|MessageSample
argument_list|(
name|msgCounter
operator|++
argument_list|,
name|timeInSec
argument_list|,
name|processed
argument_list|)
decl_stmt|;
name|model
operator|.
name|addRow
argument_list|(
name|newS
argument_list|)
expr_stmt|;
comment|// check if it's time to stop the Thread.
if|if
condition|(
name|difInMins
operator|==
name|duration
condition|)
block|{
name|Producer
operator|.
name|stopThread
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

