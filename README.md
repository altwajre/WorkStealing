# WorkStealing

In a work stealing scheduler, each processor in the computer system has a queue of work tasks to perform. while running, each task can spawn a new task or more that can feasibly be executed in parallel with its other work, it is advised but not mandatory that these new tasks will initially put on the queue of the processor executing the task. When a processor runs out of work, it looks at the queues of other processors and steals their work items. Each processor is a thread which maintains local work queue. A processor can push and pop tasks from its local queue. Also, a processor can pop tasks from other processor’s queue by the steal action, see figure below:

![alt tag] (https://github.com/ameergneem/WorkStealing/blob/master/StealingSchlr.png)

the code is written in Java, with simple “Phone manufacture” simulation and merge sort for testing.
