import { mount } from '@vue/test-utils';
import { describe, expect, it, vi } from 'vitest';
import KanbanBoard from '../KanbanBoard.vue';

vi.mock('../../../services/kanban', () => ({
  updateApplicationStage: vi.fn().mockResolvedValue({ data: { id: 1, stage: 'Interview' } })
}));

const { updateApplicationStage } = await import('../../../services/kanban');

describe('KanbanBoard', () => {
  it('renders the stage columns and updates an application stage', async () => {
    const wrapper = mount(KanbanBoard, {
      props: {
        applications: [
          { id: 1, company: 'Acme', position: 'Engineer', stage: 'Applied' },
          { id: 2, company: 'Contoso', position: 'Designer', stage: 'Interview' }
        ]
      }
    });

    expect(wrapper.text()).toContain('Applied');
    expect(wrapper.text()).toContain('Interview');

    const card = wrapper.find('[data-testid="application-card-1"]');
    await card.trigger('dragstart');

    const target = wrapper.find('[data-testid="stage-column-Interview"]');
    await target.trigger('drop');

    expect(updateApplicationStage).toHaveBeenCalledWith(1, 'Interview');
  });
});
